package com.abueltaweel.data.prayer.mapper

import com.batoulapps.adhan2.CalculationMethod

fun com.abueltaweel.domain.entity.prayer.CalculationMethod.toAdhanParams() =
    when (this) {
        com.abueltaweel.domain.entity.prayer.CalculationMethod.EGYPTIAN -> {
            CalculationMethod.EGYPTIAN.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.MUSLIM_WORLD_LEAGUE -> {
            CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.KARACHI -> {
            CalculationMethod.KARACHI.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.UMM_AL_QURA -> {
            CalculationMethod.UMM_AL_QURA.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.DUBAI -> {
            CalculationMethod.DUBAI.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.QATAR -> {
            CalculationMethod.QATAR.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.KUWAIT -> {
            CalculationMethod.KUWAIT.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.MOONSIGHTING_COMMITTEE -> {
            CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.SINGAPORE -> {
            CalculationMethod.SINGAPORE.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.NORTH_AMERICA -> {
            CalculationMethod.NORTH_AMERICA.parameters
        }

        com.abueltaweel.domain.entity.prayer.CalculationMethod.OTHER -> {
            CalculationMethod.OTHER.parameters
        }
    }