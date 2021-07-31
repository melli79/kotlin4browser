import kotlinx.serialization.*
import kotlin.js.Date

enum class Gender {
    male, female
}

enum class Relative {
    Person {
        override fun prefix(g :Gender, gen :Int) = if (g==Gender.male) "Mr." else "Ms."
    }, Parent {
        override fun prefix(g :Gender, gen :Int) :String {
            val grand = when (gen) {
                2 -> "grand-"
                3 -> "great-grand-"
                4, 5, 6, 7, 8, 9, 10, 11 -> "great^${gen-2}-grand-"
                else -> ""
            }
            return grand+ if (g==Gender.male) "father"  else "mother"
        }
    }, Spouse {
        override fun prefix(g :Gender, gen :Int) = if (g==Gender.male) "husband" else "wife"
    }, Child {
        override fun prefix(g :Gender, gen :Int) :String {
            val grand = when (-gen) {
                2 -> "grand-"
                3 -> "great-grand-"
                4, 5, 6, 7, 8, 9, 10, 11 -> "great^${gen-2}-grand-"
                else -> ""
            }
            return grand+ if (g==Gender.male) "son"  else "daughter"
        }
    }, Sibling {
        override fun prefix(g :Gender, gen :Int) :String {
            if (gen>0) {
                val grand = when (gen) {
                    2 -> "grand-"
                    3 -> "great-grand-"
                    4, 5, 6, 7, 8, 9, 10, 11 -> "great^${gen-2}-grand-"
                    else -> ""
                }
                return grand+ if (g == Gender.male) "uncle" else "aunt"
            }
            return if (g==Gender.male) "brother" else "sister"
        }
    };

    abstract fun prefix(g :Gender, gen :Int =1) :String
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
    var parents :MutableSet<Person> = mutableSetOf(),
    val isEastern :Boolean =false) {
    var spouse :Person? = null
    var children :MutableSet<Person> = mutableSetOf()
    var deathDay :PartialDate? = null

    override fun toString() = if (isEastern)
        """$familyName${givenNames.joinToString("")}"""
      else
        """${givenNames.joinToString(" ")} $familyName"""

    fun describe(r :Relative = Relative.Person, prefix :String ="", gen :Int =0) = """$prefix${r.prefix(gender, gen)} ${toString()}
        | *$birthday ${if (deathDay != null) "♧$deathDay" else ""}""".trimMargin()

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
        p.describe(Relative.Parent)
        siblings.addAll(p.children)
    }
    siblings.remove(this)
    if (spouse!=null)
        spouse!!.describe(Relative.Spouse)
    for (c in children) {
        c.describe(Relative.Child)
    }
    for (s in siblings) {
        s.describe(Relative.Sibling)
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
    val parents = mutableSetOf(this)
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

fun Person.balance(visited :MutableSet<Person> = mutableSetOf()) {
    if (this in visited)
        return
    visited.add(this)
    for (p in parents)
        if (this !in p.children)
            p.children.add(this)
    val marriedSpouse = spouse!=null && spouse!!.familyName == familyName
    for (c in children) {
        if (this !in c.parents) {
            c.parents.add(this)
        }
        if (c.parents.size<2 && marriedSpouse)
            c.parents.add(spouse!!)
    }
    if (spouse!=null) {
        val s = spouse!!
        if (marriedSpouse) {
            for (c in children) if (s in c.parents || c.parents.size<2)
                s.children.add(c)
            s.spouse = this
        }
        s.balance(visited)
    }
    for (p in parents.toTypedArray())
        p.balance(visited)
    for (c in children.toTypedArray())
        c.balance(visited)
}
