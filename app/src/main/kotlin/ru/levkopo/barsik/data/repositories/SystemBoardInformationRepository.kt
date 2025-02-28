package ru.levkopo.barsik.data.repositories

import CF.DataType
import CF.PropertiesHolder
import CF.ResourceHelper
import DSP.SigBoardInfo3Helper
import ru.levkopo.barsik.data.remote.SignalOrbManager

object SystemBoardInformationRepository {
    fun isConnected() = SignalOrbManager.useApplication { it != null }
    fun getSystemBoardInfo() = SignalOrbManager.useApplication { application ->
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
        SigBoardInfo3Helper.extract(result.value[0].value)
    }
}