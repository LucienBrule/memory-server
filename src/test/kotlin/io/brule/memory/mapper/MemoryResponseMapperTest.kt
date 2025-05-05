package io.brule.memory.mapper

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import qdrant.client.grpc.ListValue
import qdrant.client.grpc.Struct
import qdrant.client.grpc.Value
import qdrant.client.grpc.points.PointId
import qdrant.client.grpc.points.PointsOperationResponse
import qdrant.client.grpc.points.RetrievedPoint
import qdrant.client.grpc.points.ScoredPoint
import qdrant.client.grpc.points.ScrollResponse
import qdrant.client.grpc.points.SearchResponse

class MemoryResponseMapperTest {

    @Test
    fun `fromSearchResponse maps SearchResponse to MemoryRecallResponse`() {
        val scoredPoint = ScoredPoint.newBuilder()
            .setId(PointId.newBuilder().setNum(2).build())
            .putPayload("content", Value.newBuilder().setStringValue("Sample content").build())
            .putPayload("uid", Value.newBuilder().setStringValue("unique-id-1").build())
            .putPayload("summary", Value.newBuilder().setStringValue("Sample summary").build())
            .putPayload("created_at", Value.newBuilder().setStringValue("2023-06-02T12:00:00Z").build())
            .putPayload(
                "tags", Value.newBuilder().setListValue(
                    ListValue.newBuilder().addValues(
                        Value.newBuilder().setStructValue(
                            Struct.newBuilder()
                                .putFields("name", Value.newBuilder().setStringValue("tag1-name").build())
                                .putFields("value", Value.newBuilder().setStringValue("tag1-value").build())
                        ).build()
                    ).build()
                ).build()
            )
            .setScore(0.95f)
            .build()


        val searchResponse = SearchResponse.newBuilder()
            .addResult(scoredPoint)
            .setTime(123.0)
            .build()

        val response = MemoryResponseMapper.fromSearchResponse(
            searchResponse,
            page = 2,
            pageSize = 5
        )

        Assertions.assertEquals(1, response.memories.size)
        val result = response.memories[0]
        Assertions.assertEquals("unique-id-1", result.metadata.uid)
        Assertions.assertEquals("Sample content", result.content)
        Assertions.assertEquals("Sample summary", result.metadata.summary)

        Assertions.assertEquals(1, response.totalCount)
        Assertions.assertEquals(2, response.page)
        Assertions.assertEquals(5, response.pageSize)
    }

    @Test
    fun `fromUpsertResponse returns acknowledged MemoryRememberResponse`() {
        val upsertResponse = PointsOperationResponse.newBuilder().build()
        val response = MemoryResponseMapper.fromUpsertResponse(upsertResponse)
        Assertions.assertTrue(response.acknowledged)
    }

    @Test
    fun `fromScrollResponse returns default MemoryRecallResponse`() {
        val scrollResponse = ScrollResponse.newBuilder()
            .addResult(
                RetrievedPoint.newBuilder()
                    .setId(PointId.newBuilder().setNum(1).build())
                    .build()
            )
            .build()

        val response = MemoryResponseMapper.fromScrollResponse(
            scrollResponse = scrollResponse,
            page = 1,
            pageSize = 1
        )

        Assertions.assertNotNull(response)
        Assertions.assertEquals(1, response.totalCount)
        Assertions.assertEquals(1, response.page)
        Assertions.assertEquals(1, response.pageSize)
        Assertions.assertEquals(1, response.memories.size)
        val memory = response.memories[0]
        Assertions.assertEquals("", memory.content)
        Assertions.assertEquals("", memory.metadata.uid)
        Assertions.assertEquals("", memory.metadata.summary)
        Assertions.assertTrue(memory.metadata.tags.isEmpty())
    }
}