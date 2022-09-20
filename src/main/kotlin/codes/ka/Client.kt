package codes.ka

import kotlinx.datetime.Instant
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.resources.calendars
import space.jetbrains.api.runtime.types.ATimeZone
import space.jetbrains.api.runtime.types.BusyStatus
import space.jetbrains.api.runtime.types.CalendarEventSpec

suspend fun SpaceClient.createCoffeeMeeting(userId1: String, userId2: String, start: Instant, end: Instant, timezone: ATimeZone) {
    calendars.meetings.createMeeting(
        "Coffee Break",
        occurrenceRule = CalendarEventSpec(
            start,
            end,
            null,
            false,
            timezone,
            null,
            null,
            BusyStatus.Busy,
            null
        )
    )
}