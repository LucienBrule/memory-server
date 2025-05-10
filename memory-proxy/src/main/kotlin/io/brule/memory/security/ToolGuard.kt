package io.brule.memory.security

import jakarta.interceptor.InterceptorBinding

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@InterceptorBinding
annotation class ToolGuard(val expectedName: String = "")