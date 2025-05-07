package io.brule.memory.service.memory.impl.qdrant

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientInterceptor
import io.grpc.ClientCall
import io.grpc.ClientCall.Listener
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor

/**
 * A gRPC interceptor that injects the Qdrant API key into the metadata of each call.
 */
class QdrantApiKeyInterceptor(private val apiKey: String) : ClientInterceptor {

    companion object {
        private val API_KEY_HEADER: Metadata.Key<String> =
            Metadata.Key.of("api-key", Metadata.ASCII_STRING_MARSHALLER)
    }

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        val call = next.newCall(method, callOptions)
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                if (apiKey.isNotBlank()) {
                    headers.put(API_KEY_HEADER, apiKey)
                }
                super.start(responseListener, headers)
            }
        }
    }
}