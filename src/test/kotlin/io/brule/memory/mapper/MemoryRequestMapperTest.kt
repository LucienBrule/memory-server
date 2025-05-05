package io.brule.memory.mapper

import io.brule.memory.dto.MemoryDto
import io.brule.memory.dto.MemoryMetadataDto
import io.brule.memory.dto.MemoryRecallRequest
import io.brule.memory.dto.MemoryRememberRequest
import io.brule.memory.dto.MemoryTagDto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import qdrant.client.grpc.points.Filter

class MemoryRequestMapperTest {

    private val collectionName = "test-collection"
    private val id = "123e4567-e89b-12d3-a456-426614174000"
    private val vectorName = "embedding"
    private val vector = listOf(0.1, 0.2, 0.3)

    private val memoryDto = MemoryDto(
        content = "Hello world",
        metadata = MemoryMetadataDto(
            tags = listOf(
                MemoryTagDto("key1", "value1"),
                MemoryTagDto("key2", "value2")
            ),
            summary = "a greeting",
            uid = "uid123",
            createdAt = "2025-05-05T00:00:00Z"
        )
    )

    private val rememberRequest = MemoryRememberRequest(memoryDto)

    private val recallRequest = MemoryRecallRequest(
        memory = memoryDto,
        exclude = emptyList(),
        page = 2,
        pageSize = 5
    )

    @Test
    fun `toUpsertPointsRequest builds correct UpsertPoints message`() {
        val req = MemoryRequestMapper.toUpsertPointsRequest(
            request = rememberRequest,
            collectionName = collectionName,
            id = id,
            vectorName = vectorName,
            vector = vector
        )

        // collection name
        Assertions.assertEquals(collectionName, req.collectionName)

        // should contain exactly one point
        Assertions.assertEquals(1, req.pointsCount)
        val point = req.pointsList.single()

        // id should match
        Assertions.assertEquals(id, point.id.uuid)

        // vectors: ensure the named vector exists and holds the correct floats
        val named = point.vectors.vectors.vectorsMap
        Assertions.assertTrue(named.containsKey(vectorName))
        Assertions.assertEquals(vector.map(Double::toFloat), named[vectorName]!!.dataList)

        // payload: verify simple fields
        val payload = point.payloadMap
        Assertions.assertEquals(memoryDto.content, payload["content"]!!.stringValue)
        Assertions.assertEquals(memoryDto.metadata.summary, payload["summary"]!!.stringValue)
        Assertions.assertEquals(memoryDto.metadata.uid, payload["uid"]!!.stringValue)
        Assertions.assertEquals(memoryDto.metadata.createdAt, payload["created_at"]!!.stringValue)

        // tags should be serialized as a ListValue of Structs
        Assertions.assertTrue(payload.containsKey("tags"))
        val tagsList = payload["tags"]!!.listValue.valuesList
        Assertions.assertEquals(2, tagsList.size)
        val firstTag = tagsList[0].structValue.fieldsMap
        Assertions.assertEquals("key1", firstTag["name"]!!.stringValue)
        Assertions.assertEquals("value1", firstTag["value"]!!.stringValue)
    }

    @Test
    fun `toSearchPointsRequest builds correct SearchPoints message`() {
        val req = MemoryRequestMapper.toSearchPointsRequest(
            request = recallRequest,
            collectionName = collectionName,
            vectorName = vectorName,
            vector = vector
        )

        Assertions.assertEquals(collectionName, req.collectionName)
        Assertions.assertEquals(vectorName, req.vectorName)
        Assertions.assertEquals(vector.map(Double::toFloat), req.vectorList)
        // default limit is pageSize * 2
        Assertions.assertEquals((recallRequest.pageSize * 2).toLong(), req.limit)
        // offset = (page - 1) * pageSize
        Assertions.assertEquals(((recallRequest.page - 1) * recallRequest.pageSize).toLong(), req.offset)
    }

    @Test
    fun `toScrollPointsRequest builds correct ScrollPoints message`() {
        val filter = Filter.getDefaultInstance()
        val req = MemoryRequestMapper.toScrollPointsRequest(
            request = recallRequest,
            collectionName = collectionName,
            filter = filter
        )

        Assertions.assertEquals(collectionName, req.collectionName)
        Assertions.assertEquals(recallRequest.pageSize, req.limit)
        // offset is a PointId { num = ... }
        Assertions.assertEquals(((recallRequest.page - 1) * recallRequest.pageSize).toLong(), req.offset.num)
        Assertions.assertEquals(filter, req.filter)
    }
}