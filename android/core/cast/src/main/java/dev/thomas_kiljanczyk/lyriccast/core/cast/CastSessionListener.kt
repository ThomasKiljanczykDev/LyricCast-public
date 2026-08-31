/*
 * Created by Tomasz Kiljanczyk on 25/01/2025, 18:55
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 08/12/2024, 21:35
 */

package dev.thomas_kiljanczyk.lyriccast.core.cast

import com.google.android.gms.cast.framework.Session
import com.google.android.gms.cast.framework.SessionManagerListener

class CastSessionListener(
    private val onStarted: (session: Session) -> Unit,
    private val onEnded: ((session: Session) -> Unit)? = null,
    /** Session resumed after a process or connectivity gap. */
    private val onResumed: ((session: Session) -> Unit)? = null,
    /** Session dropped without ending; nothing can be sent until it resumes. */
    private val onSuspended: ((session: Session) -> Unit)? = null
) : SessionManagerListener<Session> {

    override fun onSessionStarting(session: Session) {
    }

    override fun onSessionStarted(session: Session, sessionId: String) {
        onStarted(session)
    }

    override fun onSessionStartFailed(session: Session, error: Int) {
    }

    override fun onSessionEnding(session: Session) {
    }

    override fun onSessionEnded(session: Session, error: Int) {
        onEnded?.invoke(session)
    }

    override fun onSessionResuming(session: Session, sessionId: String) {
    }

    override fun onSessionResumed(session: Session, wasSuspended: Boolean) {
        onResumed?.invoke(session)
    }

    override fun onSessionResumeFailed(session: Session, error: Int) {
    }

    override fun onSessionSuspended(session: Session, reason: Int) {
        onSuspended?.invoke(session)
    }
}
