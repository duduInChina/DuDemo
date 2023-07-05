package com.dudu.download

import com.dudu.common.util.FileUtil
import com.dudu.download.bean.DownloadStatus
import com.dudu.download.dao.DownloadTaskData

/**
 * 功能介绍
 * Created by Dzc on 2023/6/27.
 */
class DownloadConstant {
    companion object {
        const val apkUrl =
            "https://imtt.dd.qq.com/16891/myapp/channel_102497291_1165285_b0e7c10e19b54b31dfaf1fd4c0150e44.apk"

        val urlList = listOf(
            "http://imtt.dd.qq.com/sjy.40001/sjy.00001/16891/apk/69E7E4E87B798032925A2CA9D99F4F22.apk?fsname=com.tencent.ggame_1.7.4_174.apk&csr=81e7",
            "http://imtt.dd.qq.com/sjy.40001/sjy.00001/16891/apk/C2F17F4006AB308BAF0B29D102DD6566.apk?fsname=com.tencent.qqgame.qqhlupwvga_4.3.2_43020.apk&csr=81e7",
            "http://imtt.dd.qq.com/sjy.40001/sjy.00002/16891/apk/49B44FE0E45A7FF1AB02AF9B348022DC.apk?fsname=com.ourgame.mahjong.danji_7.3.19.27_31927.apk&csr=81e7",
            "http://imtt.dd.qq.com/sjy.40001/sjy.00002/16891/apk/74043F69111880A873832E75B1535AC3.apk?fsname=com.gdy.yyb_7.3.9_70309.apk&csr=81e7",
            "http://imtt.dd.qq.com/sjy.40001/sjy.00002/16891/apk/E9C56679E0F72140578F8B7AC5E3C536.apk?fsname=com.tencent.tmgp.jinxiuddz_1.27_1.apk&csr=81e7",
            "http://imtt.dd.qq.com/sjy.40001/sjy.00002/16891/apk/22EF8602EB5EB244D9B60F872FDCD91C.apk?fsname=com.tencent.tmgp.lljjyyb2_2.2.0_2200.apk&csr=81e7",
            "http://imtt.dd.qq.com/sjy.40001/sjy.00002/16891/apk/DEC4815412BFEDFDD02308835FF74999.apk?fsname=com.cbfq.srddz_9.2.9_929.apk&csr=81e7",
            "http://imtt.dd.qq.com/sjy.40001/sjy.00001/16891/apk/D09201BF06A2EAFA68833DDAEEB38A5F.apk?fsname=com.tencent.tmgp.speedmobile_1.32.0.2188_1320002188.apk&csr=81e7",
            "http://imtt.dd.qq.com/sjy.40001/sjy.00001/16891/apk/D830417642DF843242A0BD78601D1B14.apk?fsname=com.qqgame.happymj_7.7.63_77630.apk&csr=81e7",
            "http://imtt.dd.qq.com/sjy.40001/sjy.00002/16891/apk/A7120AEB0DE5C59E3415C33149F5F6BA.apk?fsname=com.tencent.tmgp.ibirdgame.doudizhu_1.3_3.apk&csr=81e7",
        )

        fun downloadStatusToText(status: DownloadStatus) = when (status) {
            is DownloadStatus.None -> "下载"
            is DownloadStatus.Waiting -> "暂停"
            is DownloadStatus.Downloading -> "暂停"
            is DownloadStatus.Stopped -> "继续下载"
            is DownloadStatus.Done -> "重新下载"
            is DownloadStatus.Failed -> "重新下载"
        }

        fun fileSizeString(downloadTaskData: DownloadTaskData) =
            FileUtil.formatFileSize(downloadTaskData.readLength) + "/" + FileUtil.formatFileSize(
                downloadTaskData.contentLength
            )

        fun downloadProgress(downloadTaskData: DownloadTaskData) =
            if (downloadTaskData.readLength > 0)
                (downloadTaskData.readLength * 100 / downloadTaskData.contentLength).toInt()
            else 0
    }
}