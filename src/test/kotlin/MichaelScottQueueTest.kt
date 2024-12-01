import com.example.MichaelScottQueue
import org.jetbrains.kotlinx.lincheck.LinChecker
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import kotlinx.coroutines.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MichaelScottQueueTest {
    private val queue = MichaelScottQueue<Int>(100)

    @Operation
    fun enqueue(element: Int): Boolean {
        return queue.enqueue(element)
    }

    @Operation
    fun dequeue(): Int? {
        return queue.dequeue()
    }

    @Operation
    @Test
    fun testOrder() {
        val addedElements = mutableListOf<Int>()
        val threadCount = 10

        runBlocking {
            val jobs = List(threadCount) { index ->
                launch {
                    addedElements.add(index)
                    enqueue(index)
                }
            }
            jobs.forEach { it.join() }

            val dequeuedElements = mutableListOf<Int>()
            for (i in 0 until threadCount) {
                dequeue()?.let { dequeuedElements.add(it) }
            }

            assertEquals(addedElements.sorted(), dequeuedElements.sorted(), "Элементы были исключены из очереди в неправильном порядке.")
        }
    }

    @Operation
    @Test
    fun testDequeueFromEmptyQueue() {
        val result = dequeue()
        assertNull(result, "Удаление из пустой очереди должно возвращать null")
    }

    @Operation
    @Test
    fun testConcurrentEnqueueDequeue() = runBlocking {
        val queue = MichaelScottQueue<Int>(100)
        val elementsToAdd = (1..100).toList()
        val dequeuedElements = mutableListOf<Int>()

        val enqueueJobs = elementsToAdd.map { element ->
            launch(Dispatchers.Default) {
                queue.enqueue(element)
            }
        }
        enqueueJobs.forEach { it.join() }

        val dequeueJobs = List(100) {
            launch(Dispatchers.Default) {
                queue.dequeue()?.let { dequeuedElements.add(it) }
            }
        }
        dequeueJobs.forEach { it.join() }

        assertEquals(elementsToAdd.toSet(), dequeuedElements.toSet(), "Не все элементы были исключены из очереди")
    }

    @Operation
    @Test
    fun testQueueOverflow() {
        val queue = MichaelScottQueue<Int>(10)
        for (i in 0 until 10) {
            queue.enqueue(i)
        }
        val result = queue.enqueue(11)
        assertEquals(false, result, "Очередь заполнена, но все еще есть возможность добавить в нее элемент")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val options = StressOptions()
                .actorsBefore(1)
                .actorsAfter(1)
                .iterations(1000)
                .threads(16)

            try {
                LinChecker.check(MichaelScottQueueTest::class.java, options)
            } catch (e: Exception) {
                println("Ошибка при выполнении тестов: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
