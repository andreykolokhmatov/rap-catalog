package band.effective.education.rapcatalog

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
