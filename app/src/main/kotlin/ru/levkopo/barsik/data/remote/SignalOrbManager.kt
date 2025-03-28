package ru.levkopo.barsik.data.remote

import CF.Application
import CF.ApplicationFactoryHelper
import CF.DataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.omg.CosNaming.NameComponent
import org.omg.PortableServer.POA
import ru.levkopo.barsik.configs.ApplicationConfig
import ru.levkopo.barsik.data.repositories.LogsRepository
import ru.levkopo.barsik.models.Either

/**
 * Логика создания приложения с помощью фабрики приложений
 */
object SignalOrbManager {
    private val orbManager = OrbManager()
    private var isInitialized = false

    private val applicationStateFlow = MutableStateFlow<Application?>(null)

    suspend fun start(): Boolean {
        LogsRepository.info(javaClass.simpleName, "Запрос инициализации")

        when {
            !isInitialized -> {
                if(!orbManager.initialize()) {
                    return false
                }

                try {
                    LogsRepository.info(javaClass.simpleName, "Получение ApplicationFactory")
                    val factory = ApplicationFactoryHelper.narrow(orbManager.namingContext.resolve(arrayOf(
                        NameComponent("DSP", ""),
                        NameComponent("NIG-5 Applications", ""),
                    )))

                    LogsRepository.info(javaClass.simpleName, "Создание приложения SNTest(profile=${ApplicationConfig.profile})")
                    applicationStateFlow.emit(
                        factory.create(
                            "SNTest",
                            arrayOf(
                                DataType("profile", orbManager.orb.create_any().apply {
                                    insert_string(ApplicationConfig.profile)
                                })
                            ),
                            arrayOf()
                        ).also {
                            LogsRepository.info(javaClass.simpleName, "Приложение создано")
                        }
                    )

                    isInitialized = true
                    return true
                }catch (e: Throwable) {
                    LogsRepository.error(javaClass.simpleName, e)
                    return false
                }
            }

            else -> {
                LogsRepository.info(javaClass.simpleName, "Инициализация не требуется")
                return true
            }
        }
    }

    fun stop() {
        isInitialized = false
        applicationStateFlow.value?.releaseObject()
        applicationStateFlow.tryEmit(null)
    }

    fun <T> usePOA(handler: (POA) -> T) {
        if (applicationStateFlow.value != null) {
            handler(orbManager.poa)
        }
    }

    fun <T> useApplication(handler: (Application?) -> T) = applicationStateFlow.map { application ->
        runCatching {
            handler(application)
        }.onSuccess {
            Either.Right(it)
        }.onFailure {
            it.printStackTrace()
            Either.Left(it)
        }
    }
}