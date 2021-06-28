import kotlinx.serialization.*
import kotlin.js.Date

enum class Gender {
    male, female
}

enum class Relative {
    person {
        override fun prefix(g :Gender) = if (g==Gender.male) "Mr." else "Ms."
    }, parent {
        override fun prefix(g :Gender) = if (g==Gender.male) "father" else "mother"
    }, spouse {
        override fun prefix(g :Gender) = if (g==Gender.male) "husband" else "wife"
    }, child {
        override fun prefix(g :Gender) = if (g==Gender.male) "son" else "daughter"
    }, sibling {
        override fun prefix(g :Gender) = if (g==Gender.male) "brother" else "sister"
    };

    abstract fun prefix(g :Gender) :String
}

@Serializable
data class PartialDate(
    val year :Int = 1900,
    val month :Int? = null,
    val day :Int? = null
) {
    constructor(date :Date) :this(date.getFullYear(), date.getMonth(), date.getDay())

    override fun toString() = """$year${if (month!=null) "–$month${if (day!=null) "–$day" else ""}" else ""}"""
}

@Serializable
class Person(
    val givenNames :Array<String>,
    val gender :Gender,
    val birthday :PartialDate,
    var familyName :String,
    var parents :MutableList<Person> = mutableListOf(),
    val isEastern :Boolean =false) {
    var spouse :Person? = null
    var children :MutableList<Person> = mutableListOf()
    var deathDay :PartialDate? = null

    override fun toString() = if (isEastern)
        """$familyName${givenNames.joinToString("")} *$birthday ${if (deathDay != null) "♧$deathDay" else ""}"""
      else
        """${givenNames.joinToString(" ")} $familyName *$birthday ${if (deathDay != null) "♧$deathDay" else ""}"""

    fun describe(r :Relative = Relative.person, prefix :String ="") = println("""$prefix${r.prefix(gender)} ${toString()}""")

    fun getFather() = parents.firstOrNull { p -> p.gender==Gender.male }
    fun getMother() = parents.firstOrNull { p -> p.gender==Gender.female }

    override fun equals(other :Any?) = other is Person &&
        givenNames.contentEquals(other.givenNames) && birthday==other.birthday

    override fun hashCode() = birthday.hashCode() +31*givenNames.hashCode()

    fun die(deathDay :PartialDate) {
        this.deathDay = deathDay
    }

    fun die(year :Int =1990, month :Int?=null, day :Int?=null) = die(PartialDate(year,month,day))
}

fun Person.details() {
    describe()
    val siblings = mutableSetOf<Person>()
    for (p in parents) {
        p.describe(Relative.parent)
        siblings.addAll(p.children)
    }
    siblings.remove(this)
    if (spouse!=null)
        spouse!!.describe(Relative.spouse)
    for (c in children) {
        c.describe(Relative.child)
    }
    for (s in siblings) {
        s.describe(Relative.sibling)
    }
}


fun Person.marry(s :Person) {
    divorce()
    s.divorce()
    spouse = s
    s.spouse = this
    s.familyName = familyName
}

fun Person.divorce() {
    if (spouse!=null) {
        spouse!!.spouse = null
        spouse = null
    }
}

fun Person.giveBirth(givenNames :Array<String>, gender :Gender, birthday :Date, father :Person? =null) =
    giveBirth(givenNames, gender, PartialDate(birthday), father)

fun Person.giveBirth(givenNames :Array<String>, gender :Gender, birthday :PartialDate, father :Person? =null) :Person {
    val parents = mutableListOf(this)
    val trueFather = father ?: spouse
    if (trueFather != null)
        parents.add(trueFather)
    val familyName = if (isEastern) trueFather?.familyName ?: familyName
        else familyName
    val child = Person(givenNames, gender, birthday,
        familyName, parents, isEastern)
    children.add(child)
    trueFather?.children?.add(child)
    return child
}

fun traverse(p :Person, src :Relative =Relative.person, prefix :String = "", visited :MutableSet<Person> = mutableSetOf()) {
    p.describe(src, prefix)
    visited.add(p)
    val newSiblings = mutableSetOf<Person>()
    val newParents = mutableListOf<Person>()
    for (par in p.parents) if (par !in visited) {
        newParents.add(par)
        visited.add(par)
    }
    for (par in newParents) {
        traverse(par, Relative.parent, prefix+"+", visited)
        for (c in par.children) if (c !in visited) {
            newSiblings.add(c)
            visited.add(c)
        }
    }
    val spouse = p.spouse
    if (spouse!=null && spouse !in visited)
        traverse(spouse, Relative.spouse, prefix+"+", visited)
    if (src !in listOf(Relative.parent, Relative.spouse)) for (c in p.children) {
        if (c !in visited)
            traverse(c, Relative.child, prefix+"+", visited)
    }
    for (s in newSiblings) {
        traverse(s, Relative.sibling, prefix+"+", visited)
    }
}
