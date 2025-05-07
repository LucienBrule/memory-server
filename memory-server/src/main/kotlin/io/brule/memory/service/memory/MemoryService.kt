package io.brule.memory.service.memory

import io.brule.memory.api.MemoryRecallRequest
import io.brule.memory.api.MemoryRecallResponse
import io.brule.memory.api.MemoryRememberRequest
import io.brule.memory.api.MemoryRememberResponse

/**
 * MemoryService provides operations to remember and recall memories.
 */
interface MemoryService {

    /**
     * Recall stored memories matching the given criteria.
     *
     * @param request the recall request containing content and tag filtering.
     * @return MemoryRecallResponse containing matched memories.
     */
    fun recall(request: MemoryRecallRequest): MemoryRecallResponse

    /**
     * Store a new memory in the system.
     *
     * @param request the remember request containing memory content and metadata.
     * @return MemoryRememberResponse acknowledging storage.
     */
    fun remember(request: MemoryRememberRequest): MemoryRememberResponse
}