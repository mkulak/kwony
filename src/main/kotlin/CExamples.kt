import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun main2(args: Array<String>) = runBlocking<Unit> {
    val channel = Channel<String>()
    val job1 = launch(CommonPool) {
        channel.send("ping")
        val msg = channel.receive()
        println("received: $msg")
    }
    val job2 = launch(CommonPool) {
        val msg = channel.receive()
        channel.send("pong")
        println("received: $msg")
    }
    job1.join()
    job2.join()
}

data class Ball(var hits: Int)

fun main(args: Array<String>) = runBlocking<Unit> {
    val table = Channel<Ball>() // a shared table
    launch(CommonPool) { player("1", table) }
    launch(CommonPool) { player("2", table) }
    launch(CommonPool) { player("3", table) }
    table.send(Ball(0)) // serve the ball
    delay(1000) // delay 1 second
    table.receive() // game over, grab the ball
}

suspend fun player(name: String, table: Channel<Ball>) {
    for (ball in table) { // receive the ball in a loop
        ball.hits++
        println("$name $ball")
        delay(30) // wait a bit
        table.send(ball) // send the ball back
    }
}
