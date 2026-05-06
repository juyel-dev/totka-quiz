package com.juyel.totkaquiz.utils

import android.content.Context
import android.media.MediaPlayer
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

object ConfettiHelper {

    fun explode(view: KonfettiView) {
        val party = Party(
            speed         = 0f,
            maxSpeed      = 30f,
            damping       = 0.9f,
            spread        = 360,
            colors        = listOf(0x6366F1, 0x06B6D4, 0xF59E0B, 0x10B981, 0xEF4444, 0x8B5CF6),
            shapes        = listOf(Shape.Circle, Shape.Square),
            size          = listOf(Size(12), Size(16)),
            position      = Position.Relative(0.5, 0.3),
            emitter       = Emitter(duration = 3, TimeUnit.SECONDS).max(300)
        )
        view.start(party)
    }

    fun rain(view: KonfettiView) {
        val parties = listOf(
            PartyFactory(Emitter(duration = 5, TimeUnit.SECONDS).perSecond(30))
                .spread(60)
                .colors(listOf(0x6366F1, 0x06B6D4, 0xF59E0B))
                .setSpeedBetween(10f, 30f)
                .position(Position.Relative(0.0, 0.0), Position.Relative(1.0, 0.0))
                .build()
        )
        view.start(*parties.toTypedArray())
    }

    fun playClapSound(context: Context) {
        try {
            val resId = context.resources.getIdentifier("clap", "raw", context.packageName)
            if (resId != 0) {
                val mp = MediaPlayer.create(context, resId)
                mp?.apply {
                    start()
                    setOnCompletionListener { release() }
                }
            }
        } catch (e: Exception) { /* ignore */ }
    }
}
