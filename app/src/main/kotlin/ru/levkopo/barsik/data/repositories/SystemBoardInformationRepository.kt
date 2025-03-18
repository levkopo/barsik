package ru.levkopo.barsik.data.repositories

import CF.DataType
import CF.PropertiesHolder
import CF.ResourceHelper
import DSP.SigBoardInfo3
import DSP.SigBoardInfo3Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import ru.levkopo.barsik.data.remote.SignalOrbManager

/**
 * Репозиторий информации о сигнальной плате
 */
object SystemBoardInformationRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _systemBoardInfo = MutableStateFlow<SigBoardInfo3?>(null)
    val systemBoardInfo = _systemBoardInfo.asStateFlow()

    init {
        SignalOrbManager.useApplication { application ->
            if(application == null) {
                return@useApplication null
            }

            val scheduler = ResourceHelper.narrow(application.getPort("Scheduler"))
            val result = PropertiesHolder(
                arrayOf(
                    DataType("SigBoardInfo3", application._get_orb().create_any()),
                )
            )

            scheduler.query(result)
            _systemBoardInfo.value = SigBoardInfo3Helper.extract(result.value[0].value)
        }.launchIn(scope)
    }
}