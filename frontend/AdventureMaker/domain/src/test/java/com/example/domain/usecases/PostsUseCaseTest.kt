package com.example.domain.usecases

import com.example.domain.entities.Post
import com.example.domain.interfaces.PostsRepository
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PostsUseCaseTest {

    private val samplePosts = listOf(
        Post(
            id = "p1",
            userId = "u1",
            imageUrl = "https://example.com/1.jpg",
            scoreValue = 5,
            description = "Nice",
            latitude = 1f,
            longitude = 2f
        )
    )

    @Test
    fun getUserPosts_delegatesToRepository() = runTest {
        val repository = mockk<PostsRepository>()
        coEvery { repository.getUserPosts("user-42") } returns Result.success(samplePosts)

        val useCase = PostsUseCase(repository)
        val result = useCase.getUserPosts("user-42")

        assertTrue(result.isSuccess)
        assertEquals(samplePosts, result.getOrNull())
        coVerify(exactly = 1) { repository.getUserPosts("user-42") }
    }

    @Test
    fun getUserPosts_whenRepositoryFails_returnsFailure() = runTest {
        val repository = mockk<PostsRepository>()
        val error = Exception("network")
        coEvery { repository.getUserPosts("u") } returns Result.failure(error)

        val result = PostsUseCase(repository).getUserPosts("u")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun loadMorePosts_callsRepository() = runTest {
        val repository = mockk<PostsRepository>()
        coJustRun { repository.loadMorePosts() }

        PostsUseCase(repository).loadMorePosts()

        coVerify(exactly = 1) { repository.loadMorePosts() }
    }
}
