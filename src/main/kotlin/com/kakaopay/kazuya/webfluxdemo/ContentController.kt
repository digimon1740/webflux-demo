package com.kakaopay.kazuya.webfluxdemo

import com.kakaopay.kazuya.webfluxdemo.dto.CompositeResponse
import com.kakaopay.kazuya.webfluxdemo.dto.ContentResponse
import com.kakaopay.kazuya.webfluxdemo.dto.UserResponse
import com.kakaopay.kazuya.webfluxdemo.entity.Content
import com.kakaopay.kazuya.webfluxdemo.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuples
import java.util.concurrent.CompletableFuture


@RestController
class ContentController(
    private val contentService: ContentService
) {
    // GET http://localhost:8080/users/1/contents/future
    @GetMapping("/users/{userId}/contents/future")
    fun getFuture(@PathVariable userId: Long): CompositeResponse =
        contentService.getAllByIdFuture(userId)

    // GET http://localhost:8080/users/2/contents/reactive
    @GetMapping("/users/{userId}/contents/reactive")
    fun getReactive(@PathVariable userId: Long): Mono<CompositeResponse> =
        contentService.getAllByIdReactive(userId)

    // GET http://localhost:8080/users/3/contents
    @GetMapping("/users/{userId}/contents")
    suspend fun get(@PathVariable userId: Long): CompositeResponse =
        contentService.getAllById(userId)

}


@Service
class ContentService(
    private val contentRepository: ContentCouroutineRepository,
    private val userCoroutineRepository: UserCoroutineRepository,
    private val contentReactiveRepository: ContentReactiveRepository,
    private val userReactiveRepository: UserReactiveRepository,
) {

    val logger = LoggerFactory.getLogger(this.javaClass)

    // 쉬운 병렬처리 but 기능이 단순하다.
    fun getAllByIdFuture(id: Long): CompositeResponse {
        // Reactor의 Publisher는 Future를 반환하는 toFuture 함수가 있지만 MVC 라고 가정
        val userFuture =
            CompletableFuture.supplyAsync { userReactiveRepository.findById(id).block()!! }
        val contentFuture =
            CompletableFuture.supplyAsync { contentReactiveRepository.findAllByUserId(id).collectList().block() }
        return CompositeResponse(UserResponse(userFuture.join()), contentFuture.join().map(ContentResponse::invoke))
    }

    // 다양한 기능을 제공하지만 복잡하다.
    fun getAllByIdReactive(id: Long): Mono<CompositeResponse> =
        contentReactiveRepository.findAllByUserId(id)
            .collectList()
            .zipWith(Mono.defer { userReactiveRepository.findById(id) })
            .flatMap { tuple: Tuple2<MutableList<Content>, User> ->
                Mono.just(Tuples.of(UserResponse(tuple.t2), tuple.t1.map(ContentResponse::invoke)))
            }.flatMap {
                Mono.just(CompositeResponse(it.t1, it.t2))
            }

    // 코루틴은 그나마 직관적이다.
    suspend fun getAllById(id: Long): CompositeResponse = withContext(Dispatchers.IO) {
        val contentDeferred = async {
            contentRepository.findAllByUserId(id).toList().map(ContentResponse::invoke)
        }
        val userDeferred = async {
            userCoroutineRepository.findById(id)!!
        }
        CompositeResponse(UserResponse(userDeferred.await()), contentDeferred.await())
    }

}


// ReactiveCrudRepository
interface ContentReactiveRepository : ReactiveCrudRepository<Content, Long> {
    fun findAllByUserId(id: Long): Flux<Content>
}

interface UserReactiveRepository : ReactiveCrudRepository<User, Long>

// CoroutineCrudRepository
interface ContentCouroutineRepository : CoroutineCrudRepository<Content, Long> {
    fun findAllByUserId(id: Long): Flow<Content>
}

interface UserCoroutineRepository : CoroutineCrudRepository<User, Long>

