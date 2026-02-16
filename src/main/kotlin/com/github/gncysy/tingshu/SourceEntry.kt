package com.github.gncysy.tingshu

import com.github.eprendre.tingshu.core.SourceEntry
import com.github.eprendre.tingshu.core.TingShu

class SourceEntry : SourceEntry {
    override fun getSources(): List<TingShu> {
        return listOf(LeTing8Source())
    }
}
