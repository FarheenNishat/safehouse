package com.safehouse.bodyguard.output

import android.content.IntentFilter

class UrlOuputIntent {
    companion object {
        fun getFilter(): IntentFilter? {
            return IntentFilter(URL_ACTION)
        }

        const val URL_ACTION = "com.safehouse.bodyguard.output.urldata"
        const val URL_KEY = "URL_KEY"
    }


}