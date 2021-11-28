enum class KnownRegion(prefix :String, val displayName :String) {
    CHINA("China", "P.R. of China"),
    EUROPE("Europe", "Europe"),
    GERMANY("Germany", "Germany"),
    N_AMERICA("nAmerica", "Northern America");

    val n = name
    val mapImage = "$prefix.jpg"
    val mapDetails = "cities.$prefix.json"
    override fun toString() = displayName
}
