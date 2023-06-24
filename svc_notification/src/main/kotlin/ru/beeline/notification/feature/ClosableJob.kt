package ru.beeline.notification.feature

import java.io.Closeable

interface ClosableJob : Closeable, Runnable