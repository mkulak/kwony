package com.xap4o.kwony.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface Logging {
  val LOG: Logger
        get() = LoggerFactory.getLogger(this.javaClass)
}
