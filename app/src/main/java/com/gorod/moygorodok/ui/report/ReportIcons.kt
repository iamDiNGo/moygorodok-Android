package com.gorod.moygorodok.ui.report

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.ReportReason
import com.gorod.moygorodok.data.model.ReportStatus

@DrawableRes
fun iconResFor(reason: ReportReason): Int = when (reason) {
    ReportReason.SPAM -> R.drawable.ic_report_spam
    ReportReason.ABUSE -> R.drawable.ic_report_abuse
    ReportReason.FALSE_INFO -> R.drawable.ic_report_false_info
    ReportReason.WRONG_DATA -> R.drawable.ic_report_wrong_data
    ReportReason.FRAUD -> R.drawable.ic_report_fraud
    ReportReason.INAPPROPRIATE -> R.drawable.ic_report_inappropriate
    ReportReason.DUPLICATE -> R.drawable.ic_report_duplicate
    ReportReason.OTHER -> R.drawable.ic_report_other
}

@ColorRes
fun colorResFor(status: ReportStatus): Int = when (status) {
    ReportStatus.PENDING -> R.color.report_pending
    ReportStatus.ACCEPTED -> R.color.report_accepted
    ReportStatus.REJECTED -> R.color.report_rejected
}

fun statusLabelRes(status: ReportStatus): Int = when (status) {
    ReportStatus.PENDING -> R.string.report_status_pending
    ReportStatus.ACCEPTED -> R.string.report_status_accepted
    ReportStatus.REJECTED -> R.string.report_status_rejected
}
