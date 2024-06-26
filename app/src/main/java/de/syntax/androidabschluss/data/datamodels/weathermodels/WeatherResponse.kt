import com.squareup.moshi.Json
import de.syntax.androidabschluss.data.datamodels.weathermodels.Clouds
import de.syntax.androidabschluss.data.datamodels.weathermodels.Coord
import de.syntax.androidabschluss.data.datamodels.weathermodels.Main
import de.syntax.androidabschluss.data.datamodels.weathermodels.Rain
import de.syntax.androidabschluss.data.datamodels.weathermodels.Snow
import de.syntax.androidabschluss.data.datamodels.weathermodels.Sys
import de.syntax.androidabschluss.data.datamodels.weathermodels.Weather
import de.syntax.androidabschluss.data.datamodels.weathermodels.Wind

data class WeatherResponse(

    @Json(name = "coord")
    val coord: Coord,

    @Json(name = "weather")
    val weather: List<Weather>,

    @Json(name = "base")
    val base: String,

    @Json(name = "main")
    val main: Main,

    @Json(name = "visibility")
    val visibility: Int,

    @Json(name = "wind")
    val wind: Wind,

    @Json(name = "clouds")
    val clouds: Clouds,

    @Json(name = "dt")
    val dt: Int,

    @Json(name = "sys")
    val sys: Sys,

    @Json(name = "timezone")
    val timezone: Int,

    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "cod")
    val cod: Int,

    @Json(name = "snow")
    val snow: Snow?,

    @Json(name = "rain")
    val rain: Rain?


)

