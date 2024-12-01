package com.example

import java.util.concurrent.atomic.AtomicReference

class MichaelScottQueue<T>(private val capacity: Int) {

    private val head = AtomicReference(Node<T>(null))
    private val tail = AtomicReference<Node<T>>(head.get())

    private class Node<T>(val value: T?) {
        val next = AtomicReference<Node<T>?>(null)
    }

    fun enqueue(value: T): Boolean {
        val newNode = Node(value)
        while (true) {
            val currentTail = tail.get()
            val next = currentTail.next.get()

            if (currentTail === tail.get()) {
                if (next == null) {
                    val currentSize = getSize()
                    if (currentSize >= capacity) {
                        println("Queue is full, cannot enqueue: $value")
                        return false
                    }
                    if (currentTail.next.compareAndSet(null, newNode)) {
                        tail.compareAndSet(currentTail, newNode)
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
                        val value = next.value
                        if (head.compareAndSet(currentHead, next)) {
                            println("Dequeued: $value")
                            return value
                        }
                    }
                }
            }
        }
    }

    fun getSize(): Int {
        var count = 0
        var current = head.get().next.get()
        while (current != null) {
            count++
            current = current.next.get()
        }
        return count
    }
}

fun main() {
    val queue = MichaelScottQueue<Int>(1000)
    val executor = java.util.concurrent.Executors.newFixedThreadPool(16)

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
    executor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES)

    println("Final queue size: ${queue.getSize()}")
}
