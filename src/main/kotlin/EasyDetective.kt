
class EasyDetective(override val name :String = "Inspector Gadget", val alibis :Scenario, val background :CrimeSceneProps) :Detective {
    private val observations = mutableListOf<Observation>()
    init {
        observations.add(Observation(name, alibis, emptyList(), 1, null))
    }

    override fun createInquiry() :Scenario? {
        cl@for (c in background.criminals) al@for (a in background.actions)
            kl@for (k in background.kinds) wl@for (w in background.weapons)
                ml@for (m in background.motives) {
                    if (c in observations.map { o -> o.alibiItem })
                        continue@cl
                    if (a in observations.map { o -> o.alibiItem })
                        continue@al
                    if (k in observations.map { o -> o.alibiItem })
                        continue@kl
                    if (w in observations.map { o -> o.alibiItem })
                        continue@wl
                    if (m in observations.map { o -> o.alibiItem })
                        continue@ml
                    val candidate = Scenario(c, a, k, w, m)
                    if (candidate !in observations.map { o -> o.scene })
                        return candidate
                }
        return null
    }

    override fun tellObservation(observation :Observation) {
        observations.add(observation)
    }
}
