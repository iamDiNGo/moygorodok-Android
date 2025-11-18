package com.gorod.moygorodok.data.model

data class Movie(
    val id: String,
    val title: String,
    val originalTitle: String,
    val posterColor: String,
    val genre: String,
    val duration: Int, // in minutes
    val rating: Float,
    val ageRating: String,
    val description: String,
    val director: String,
    val cast: List<String>,
    val releaseDate: String
)

data class Cinema(
    val id: String,
    val name: String,
    val address: String,
    val color: String
)

data class Showtime(
    val id: String,
    val movieId: String,
    val cinema: Cinema,
    val hall: String,
    val time: String,
    val date: String,
    val price: Int,
    val availableSeats: Int,
    val format: String // 2D, 3D, IMAX
)

data class MovieWithShowtimes(
    val movie: Movie,
    val showtimes: List<Showtime>
)

object MockCinemas {

    private val cinemas = listOf(
        Cinema(
            id = "1",
            name = "Кинотеатр Россия",
            address = "ул. Ленина, 45",
            color = "#E91E63"
        ),
        Cinema(
            id = "2",
            name = "Синема Парк",
            address = "ТРЦ Галерея, 3 этаж",
            color = "#2196F3"
        ),
        Cinema(
            id = "3",
            name = "Премьер Зал",
            address = "пр. Мира, 112",
            color = "#FF9800"
        )
    )

    private val movies = listOf(
        Movie(
            id = "1",
            title = "Звёздные войны: Новая надежда",
            originalTitle = "Star Wars: A New Hope",
            posterColor = "#1A237E",
            genre = "Фантастика, Приключения",
            duration = 121,
            rating = 8.6f,
            ageRating = "12+",
            description = "Принцесса Лея захвачена в плен Дартом Вейдером. Люк Скайуокер и капитан Хан Соло пытаются её спасти и заодно уничтожить «Звезду Смерти».",
            director = "Джордж Лукас",
            cast = listOf("Марк Хэмилл", "Харрисон Форд", "Кэрри Фишер"),
            releaseDate = "25 мая 1977"
        ),
        Movie(
            id = "2",
            title = "Интерстеллар",
            originalTitle = "Interstellar",
            posterColor = "#263238",
            genre = "Фантастика, Драма",
            duration = 169,
            rating = 8.7f,
            ageRating = "16+",
            description = "Когда засуха приводит человечество к продовольственному кризису, группа исследователей отправляется через червоточину в поисках нового дома.",
            director = "Кристофер Нолан",
            cast = listOf("Мэттью Макконахи", "Энн Хэтэуэй", "Джессика Честейн"),
            releaseDate = "26 октября 2014"
        ),
        Movie(
            id = "3",
            title = "Начало",
            originalTitle = "Inception",
            posterColor = "#37474F",
            genre = "Фантастика, Боевик",
            duration = 148,
            rating = 8.8f,
            ageRating = "16+",
            description = "Кобб способен проникать в чужие сны и красть самое ценное — идеи. Теперь ему предстоит совершить обратное — внедрить мысль в сознание человека.",
            director = "Кристофер Нолан",
            cast = listOf("Леонардо ДиКаприо", "Джозеф Гордон-Левитт", "Эллен Пейдж"),
            releaseDate = "8 июля 2010"
        ),
        Movie(
            id = "4",
            title = "Джокер",
            originalTitle = "Joker",
            posterColor = "#4A148C",
            genre = "Драма, Триллер",
            duration = 122,
            rating = 8.4f,
            ageRating = "18+",
            description = "Готэм, начало 1980-х годов. Комик Артур Флек живёт с больной матерью, которая с детства учит его «ходить с улыбкой».",
            director = "Тодд Филлипс",
            cast = listOf("Хоакин Феникс", "Роберт Де Ниро", "Зази Битц"),
            releaseDate = "3 октября 2019"
        ),
        Movie(
            id = "5",
            title = "Дюна",
            originalTitle = "Dune",
            posterColor = "#BF360C",
            genre = "Фантастика, Приключения",
            duration = 155,
            rating = 8.0f,
            ageRating = "12+",
            description = "Наследник знаменитого дома Атрейдесов Пол отправляется вместе с семьёй на планету Арракис, чтобы обеспечить безопасность будущего своей семьи.",
            director = "Дени Вильнёв",
            cast = listOf("Тимоти Шаламе", "Ребекка Фергюсон", "Оскар Айзек"),
            releaseDate = "16 сентября 2021"
        ),
        Movie(
            id = "6",
            title = "Паразиты",
            originalTitle = "Parasite",
            posterColor = "#1B5E20",
            genre = "Драма, Триллер",
            duration = 132,
            rating = 8.5f,
            ageRating = "18+",
            description = "Обычное корейское семейство жить в подвале без средств к существованию. Однажды старший сын устраивается репетитором в богатую семью.",
            director = "Пон Джун-хо",
            cast = listOf("Сон Кан-хо", "Ли Сон-гюн", "Чо Ё-джон"),
            releaseDate = "30 мая 2019"
        )
    )

    fun getMovies(): List<Movie> = movies

    fun getCinemas(): List<Cinema> = cinemas

    fun getMovieById(id: String): Movie? = movies.find { it.id == id }

    fun getShowtimesForMovie(movieId: String): List<Showtime> {
        val showtimes = mutableListOf<Showtime>()
        var showtimeId = 1

        cinemas.forEach { cinema ->
            val times = listOf("10:00", "12:30", "15:00", "17:30", "20:00", "22:30")
            val halls = listOf("Зал 1", "Зал 2", "Зал 3", "VIP")
            val formats = listOf("2D", "3D", "IMAX")

            times.shuffled().take(3).forEach { time ->
                showtimes.add(
                    Showtime(
                        id = (showtimeId++).toString(),
                        movieId = movieId,
                        cinema = cinema,
                        hall = halls.random(),
                        time = time,
                        date = "Сегодня",
                        price = listOf(250, 300, 350, 400, 450).random(),
                        availableSeats = (5..50).random(),
                        format = formats.random()
                    )
                )
            }
        }

        return showtimes.sortedBy { it.time }
    }

    fun getNowPlayingCount(): Int = movies.size

    fun getUpcomingMovies(): List<String> = listOf("Аватар 3", "Мстители 5", "Бэтмен 2")
}
