// MemoryRememberRequestExtensions.kt
package io.brule.memory.mapper

import io.brule.memory.dto.MemoryDto
import qdrant.client.grpc.Value
import qdrant.client.grpc.value        // the inline fun from ValueKt
import qdrant.client.grpc.struct       // from StructKt
import qdrant.client.grpc.listValue    // from ListValueKt

fun MemoryDto.toPayloadMap(): Map<String, Value> = buildMap {
    // simple string fields
    put("content",   value { stringValue = content })
    put("summary",   value { stringValue = metadata.summary })
    put("uid",       value { stringValue = metadata.uid })
    put("created_at",value { stringValue = metadata.createdAt })

    // tags as a JSON‐style list of structs
    if (metadata.tags.isNotEmpty()) {
        put("tags", value {
            // wrap our tags in a ListValue
            listValue = listValue {
                metadata.tags.forEach { tag ->
                    // each tag → a struct { name: "...", value: "..." }
                    values += value {
                        structValue = struct {
                            fields["name"]  = value { stringValue = tag.name  }
                            fields["value"] = value { stringValue = tag.value }
                        }
                    }
                }
            }
        })
    }
}