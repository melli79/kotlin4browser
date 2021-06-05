
interface Detective {
    val name :String
    fun createInquiry() :Scenario?
    fun tellObservation(observation :Observation)
}
