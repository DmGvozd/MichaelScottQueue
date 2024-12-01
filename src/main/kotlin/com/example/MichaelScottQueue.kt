package com.example

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MichaelScottQueue<T>(private val capacity: Int) {
    private val head = AtomicReference<Node<T>>(Node(null))
    private val tail = AtomicReference<Node<T>>(head.get())
    private val size = AtomicInteger(0)

    private class Node<T>(val value: T?) {
        val next = AtomicReference<Node<T>?>(null)
    }

    fun enqueue(value: T): Boolean {
        if (size.get() >= capacity) {
            println("Queue is full, cannot enqueue: $value")
            return false
        }

        val newNode = Node(value)
        while (true) {
            val currentTail = tail.get()
            val next = currentTail.next.get()

            if (currentTail === tail.get()) {
                if (next == null) {
                    if (currentTail.next.compareAndSet(null, newNode)) {
                        tail.compareAndSet(currentTail, newNode)
                        size.incrementAndGet()
                        println("Enqueued: $value")
                        return true
                    }
                } else {
                    tail.compareAndSet(currentTail, next)
                }
            }
        }
    }

    fun dequeue(): T? {
        while (true) {
            val currentHead = head.get()
            val currentTail = tail.get()
            val next = currentHead.next.get()

            if (currentHead === head.get()) {
                if (currentHead === currentTail) {
                    if (next == null) {
                        println("Queue is empty, cannot dequeue")
                        return null
                    }
                    tail.compareAndSet(currentTail, next)
                } else {
                    if (next != null) {
                        head.compareAndSet(currentHead, next)
                        size.decrementAndGet()
                        println("Dequeued: ${next.value}")
                        return next.value
                    }
                }
            }
        }
    }

    fun getSize(): Int {
        return size.get()
    }
}

fun main() {
    val queue = MichaelScottQueue<Int>(1000)
    val executor = Executors.newFixedThreadPool(16)

    for (i in 1..200) {
        executor.submit {
            val success = queue.enqueue(i)
            if (!success) {
                println("Failed to enqueue: $i")
            }
        }
    }

    for (i in 1..200) {
        executor.submit {
            val value = queue.dequeue()
            if (value != null) {
                println("Successfully dequeued: $value")
            }
        }
    }

    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.MINUTES)

    println("Final queue size: ${queue.getSize()}")
}
