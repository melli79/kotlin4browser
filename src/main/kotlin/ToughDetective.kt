
class ToughDetective(override val name :String = "B.F. Skinner", val alibis :Scenario, val background :CrimeSceneProps) :Detective {
    private val observations = mutableListOf<Observation>()
    private var lastScenario :Scenario? = null
    init {
        observations.add(Observation(name, alibis, emptyList(), 1, alibis.criminal))
        observations.add(Observation(name, alibis, emptyList(), 1, alibis.action))
        observations.add(Observation(name, alibis, emptyList(), 1, alibis.kind))
        observations.add(Observation(name, alibis, emptyList(), 1, alibis.weapon))
        observations.add(Observation(name, alibis, emptyList(), 1, alibis.motive))
    }

    override fun createInquiry() :Scenario? {
        if (lastScenario!=null) {
            val lastAlibiItem = observations.map { o -> o.alibiItem }.filterNotNull().lastOrNull()
            if (lastAlibiItem!=null) {
                when (lastAlibiItem) {
                    in background.criminals -> updateCriminal()
                    in background.actions -> updateAction()
                    in background.kinds -> updateKind()
                    in background.weapons -> updateWeapon()
                    else -> updateMotive()
                }
            }
        }
        if (lastScenario==null)
            lastScenario = initScenario()
        return lastScenario
    }

    private fun updateCriminal() {
        if (lastScenario==null)  return
        for (criminal in background.criminals) {
            if (observations.any { o -> criminal==o.alibiItem})
                continue
            var candidate :Scenario? = Scenario(criminal, lastScenario!!.action, lastScenario!!.kind, lastScenario!!.weapon,
                lastScenario!!.motive)
            var pass = false
            mid@while (!pass) {
                pass = true
                for (observation in observations) {
                    if (candidate == observation.scene) {
                        candidate = nextExceptCriminal(candidate)
                        if (candidate!=null) {
                            pass = false
                            continue@mid
                        }
                        break
                    }
                }
                if (candidate!=null) {
                    lastScenario = candidate
                    return
                }
            }
            lastScenario = null
        }
    }

    private fun nextExceptCriminal(sc :Scenario) :Scenario? {
        val ai = background.actions.indexOf(sc.action)
        if (ai+1<background.actions.size)
            return Scenario(sc.criminal, background.actions[ai+1], sc.kind, sc.weapon, sc.motive)
        val ki = background.kinds.indexOf(sc.kind)
        if (ki+1<background.kinds.size)
            return Scenario(sc.criminal, sc.action, background.kinds[ki+1], sc.weapon, sc.motive)
        val wi = background.weapons.indexOf(sc.weapon)
        if (wi+1<background.weapons.size)
            return Scenario(sc.criminal, sc.action, sc.kind, background.weapons[wi+1], sc.motive)
        val mi = background.motives.indexOf(sc.motive)
        if (mi+1<background.motives.size)
            return Scenario(sc.criminal, sc.action, sc.kind, sc.weapon, background.motives[mi+mi])
        return null
    }

    private fun updateAction() {
        if (lastScenario==null)  return
        for (action in background.actions) {
            if (observations.any { o -> action==o.alibiItem})
                continue
            var candidate :Scenario? = Scenario(lastScenario!!.criminal, action, lastScenario!!.kind, lastScenario!!.weapon,
                lastScenario!!.motive)
            var pass = false
            mid@while (!pass) {
                pass = true
                for (observation in observations) {
                    if (candidate == observation.scene) {
                        candidate = nextExceptAction(candidate)
                        if (candidate!=null) {
                            pass = false
                            continue@mid
                        }
                        break
                    }
                }
                if (candidate!=null) {
                    lastScenario = candidate
                    return
                }
            }
            lastScenario = null
        }
    }

    private fun nextExceptAction(sc :Scenario) :Scenario? {
        val ci = background.criminals.indexOf(sc.criminal)
        if (ci+1<background.criminals.size)
            return Scenario(background.criminals[ci+1], sc.action, sc.kind, sc.weapon, sc.motive)
        val ki = background.kinds.indexOf(sc.kind)
        if (ki+1<background.kinds.size)
            return Scenario(sc.criminal, sc.action, background.kinds[ki+1], sc.weapon, sc.motive)
        val wi = background.weapons.indexOf(sc.weapon)
        if (wi+1<background.weapons.size)
            return Scenario(sc.criminal, sc.action, sc.kind, background.weapons[wi+1], sc.motive)
        val mi = background.motives.indexOf(sc.motive)
        if (mi+1<background.motives.size)
            return Scenario(sc.criminal, sc.action, sc.kind, sc.weapon, background.motives[mi+mi])
        return null
    }

    private fun updateKind() {
        if (lastScenario==null)  return
        for (kind in background.kinds) {
            if (observations.any { o -> kind==o.alibiItem})
                continue
            var candidate :Scenario? = Scenario(lastScenario!!.criminal, lastScenario!!.action, kind,
                lastScenario!!.weapon, lastScenario!!.motive)
            var pass = false
            mid@while (!pass) {
                pass = true
                for (observation in observations) {
                    if (candidate == observation.scene) {
                        candidate = nextExceptKind(candidate)
                        if (candidate!=null) {
                            pass = false
                            continue@mid
                        }
                        break
                    }
                }
                if (candidate!=null) {
                    lastScenario = candidate
                    return
                }
            }
            lastScenario = null
        }
    }

    private fun nextExceptKind(sc :Scenario) :Scenario? {
        val ci = background.criminals.indexOf(sc.criminal)
        if (ci+1<background.criminals.size)
            return Scenario(background.criminals[ci+1], sc.action, sc.kind, sc.weapon, sc.motive)
        val ai = background.actions.indexOf(sc.action)
        if (ai+1<background.kinds.size)
            return Scenario(sc.criminal, background.actions[ai+1], sc.kind, sc.weapon, sc.motive)
        val wi = background.weapons.indexOf(sc.weapon)
        if (wi+1<background.weapons.size)
            return Scenario(sc.criminal, sc.action, sc.kind, background.weapons[wi+1], sc.motive)
        val mi = background.motives.indexOf(sc.motive)
        if (mi+1<background.motives.size)
            return Scenario(sc.criminal, sc.action, sc.kind, sc.weapon, background.motives[mi+mi])
        return null
    }

    private fun updateWeapon() {
        if (lastScenario==null)  return
        for (weapon in background.weapons) {
            if (observations.any { o -> weapon==o.alibiItem})
                continue
            var candidate :Scenario? = Scenario(lastScenario!!.criminal, lastScenario!!.action, lastScenario!!.kind,
                weapon, lastScenario!!.motive)
            var pass = false
            mid@while (!pass) {
                pass = true
                for (observation in observations) {
                    if (candidate == observation.scene) {
                        candidate = nextExceptWeapon(candidate)
                        if (candidate!=null) {
                            pass = false
                            continue@mid
                        }
                        break
                    }
                }
                if (candidate!=null) {
                    lastScenario = candidate
                    return
                }
            }
            lastScenario = null
        }
    }

    private fun nextExceptWeapon(sc :Scenario) :Scenario? {
        val ci = background.criminals.indexOf(sc.criminal)
        if (ci+1<background.criminals.size)
            return Scenario(background.criminals[ci+1], sc.action, sc.kind, sc.weapon, sc.motive)
        val ai = background.actions.indexOf(sc.action)
        if (ai+1<background.actions.size)
            return Scenario(sc.criminal, background.actions[ai+1], sc.kind, sc.weapon, sc.motive)
        val ki = background.kinds.indexOf(sc.kind)
        if (ki+1<background.kinds.size)
            return Scenario(sc.criminal, sc.action, background.kinds[ki+1], sc.weapon, sc.motive)
        val mi = background.motives.indexOf(sc.motive)
        if (mi+1<background.motives.size)
            return Scenario(sc.criminal, sc.action, sc.kind, sc.weapon, background.motives[mi+mi])
        return null
    }

    private fun updateMotive() {
        if (lastScenario==null)  return
        for (motive in background.motives) {
            if (observations.any { o -> motive==o.alibiItem})
                continue
            var candidate :Scenario? = Scenario(lastScenario!!.criminal, lastScenario!!.action, lastScenario!!.kind,
                lastScenario!!.weapon, motive)
            var pass = false
            mid@while (!pass) {
                pass = true
                for (observation in observations) {
                    if (candidate == observation.scene) {
                        candidate = nextExceptMotive(candidate)
                        if (candidate!=null) {
                            pass = false
                            continue@mid
                        }
                        break
                    }
                }
                if (candidate!=null) {
                    lastScenario = candidate
                    return
                }
            }
            lastScenario = null
        }
    }

    private fun nextExceptMotive(sc :Scenario) :Scenario? {
        val ci = background.criminals.indexOf(sc.criminal)
        if (ci+1<background.criminals.size)
            return Scenario(background.criminals[ci+1], sc.action, sc.kind, sc.weapon, sc.motive)
        val ai = background.actions.indexOf(sc.action)
        if (ai+1<background.actions.size)
            return Scenario(sc.criminal, background.actions[ai+1], sc.kind, sc.weapon, sc.motive)
        val ki = background.kinds.indexOf(sc.kind)
        if (ki+1<background.kinds.size)
            return Scenario(sc.criminal, sc.action, background.kinds[ki+1], sc.weapon, sc.motive)
        val wi = background.weapons.indexOf(sc.weapon)
        if (wi+1<background.weapons.size)
            return Scenario(sc.criminal, sc.action, sc.kind, background.weapons[wi+1], sc.motive)
        return null
    }

    private fun initScenario() :Scenario? {
        cl@ for (c in background.criminals) al@ for (a in background.actions)
            kl@ for (k in background.kinds) wl@ for (w in background.weapons)
                ml@ for (m in background.motives) {
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
