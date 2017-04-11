import java.util.Collections

fun main(args: Array<String>) {
    val words = mapOf(
            "der Koffer" to "suitcase",
            "der Rucksack" to "backpack",
            "die Reisetasche" to "travel bag",
            "die Handtasche" to "hand bag",
            "die Badehose" to "swimming trunks",
            "der Anzug" to "suite",             
            "die Jeans" to "jeans",
            "das T-Shirt" to "T-Shirt",
            "die Turnshuhe" to "sneakers",
            "die Regenjacke" to "rain jacket",
            "die Socken" to "socks",
            "die Schlafanzug" to "pijama",
            "der Bikini" to "bikini",
            "der Rock" to "skirt",
            "die Bluse" to "blouse",
            "der Pullover" to "pullover",
            "die Absatzschuhe" to "shoes with heels",
            "die Strumpfhose" to "tights",
            "das Nachthemd" to "sleeping gown",
            "das Kleid" to "dress",
            "die Hose" to "pants",
            "der Mantel" to "coat",
            "das Geld" to "money",
            "der Pass" to "passport",
            "die Sonnencreme" to "sun cream",
            "die Kreditkarte" to "credit card",
            "der Fotoapparat" to "photo camera",
            "das Handy" to "mobile phone",
            "der Laptop" to "laptop",
            "der Fuhrerschein" to "driver license",
            "das Aspirin" to "aspirin",
            "die Sonnenbrille" to "sunglasses",
            "das Regenschirm" to "umbrella"
    )
    words.keys.toList().shuffle().joinToString("\n").println()
}

fun <T> List<T>.shuffle() = this.apply { Collections.shuffle(this) }

fun Any.println() = println(this)



