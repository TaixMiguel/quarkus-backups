package com.github.taixmiguel.qbs.driven.storage.mega.configuration

import dev.carlsen.mega.Mega
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces

class MegaProducer {
    @Produces
    @ApplicationScoped
    fun produceMega(): Mega = Mega()
}
