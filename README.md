# MichaelScottQueue
aka "Сопроводительный документ"

# Запуск
- Открыть файл [MichaelScottQueueTest.kt](src%2Ftest%2Fkotlin%2FMichaelScottQueueTest.kt) и запустить main.

# Список коммитов и их мотивация
1. Initial commit (https://github.com/DmGvozd/MichaelScottQueue/commit/e8ea37af41b412efab142e6edd690c4820d50443)
- Мотивация: сдать домашнее задание
2. MichaelScottQueue was added. (https://github.com/DmGvozd/MichaelScottQueue/commit/58938bb6347802a3e172120202940892772fe7d2)
- Мотивация: реализовать очередь
3. Bug fix. (https://github.com/DmGvozd/MichaelScottQueue/commit/544f979c7b8e71790579a1f27b9c05e55c50e9af)
- Мотивация: исправить ошибку в реализации из-за которой операции добавления и удаления могли работать некорректно
4. Test for dequeue order was added. (https://github.com/DmGvozd/MichaelScottQueue/commit/4a12419bcb5d98a0acc568a364d52680e97f5080)
- Мотивация: добавить проверку на извлечение элементов в том порядке, в котором они были положены в очередь.
5. Test Deq. from empty was added. (https://github.com/DmGvozd/MichaelScottQueue/commit/77e694bef84f2ebaced5f00fe085c3622872c47e)
- Мотивация: проверить, что извлечение из пустой очереди и вправду возвращает null.
6. Concurrent enqueue-dequeue test was added. (https://github.com/DmGvozd/MichaelScottQueue/commit/b4854a0ccb8d008ac726307485201e469fdd033e)
- Мотивация: проверить, что очередь корректно работает при условии параллельного доступа, гарантируя, что все добавленные элементы в итоге будут извлечены из очереди и что не возникнет никаких проблем при одновременном добавлении и удалении элементов
7. Test for queue overflow was added. (https://github.com/DmGvozd/MichaelScottQueue/commit/228fcc03027a8df45c9e2bab4c943eac8706f741)
- Мотивация: проверить, что добавление элемента в полностью заполненную очередь невозможно (то есть при добавлении возвращается false)
