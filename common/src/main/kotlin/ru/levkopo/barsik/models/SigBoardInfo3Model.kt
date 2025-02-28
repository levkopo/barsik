package ru.levkopo.barsik.models

import DSP.AttenuatorSet
import DSP.SigBoardInfo3

fun SigBoardInfo3.asString(): String {
    return "SigBoardInfo3{\n" +
            "IdRcv=" + IdRcv +
            ",\n IdCon=" + IdCon +
            ",\n IdSwt=" + IdSwt +
            ",\n filters=" + filters.contentToString() +
            ",\n simpleFreq=" + simpleFreq.contentToString() +
            ",\n rangeBands=" + rangeBands.contentToString() +
            ",\n watchTimes=" + watchTimes.contentToString() +
            ",\n winParams=" + winParams.contentToString() +
            ",\n superRangeBands=" + superRangeBands.contentToString() +
            ",\n attenuator=" + attenuator.asString() +
            ",\n allGroupRangeBands=" + allGroupRangeBands.contentToString() +
            ",\n a=" + a +
            ",\n minFreq=" + minFreq +
            ",\n maxFreq=" + maxFreq +
            '}'
}

@Override
fun AttenuatorSet.asString(): String {
    return "AttenuatorSet{" +
            "bands=" + bands.joinToString(",") {
        with(it) {
            "band_t{" +
                    "id=" + id +
                    ", begfreq=" + begfreq +
                    ", endfreq=" + endfreq +
                    '}'
        }
    } +
            ", values=" + values.joinToString(",") {
        with(it) {
            "att_entry_t{" +
                    "band_id=" + band_id +
                    ", freq=" + freq
            "}"
        }
    } +
            '}'
}
