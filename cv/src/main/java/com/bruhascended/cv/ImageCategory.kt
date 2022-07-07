package com.bruhascended.cv

enum class ImageCategory(
    val fullName: String,
    val description: String,
    val thumbnail: String,
) {
    Assignment(
        "Assignments and Study Material",
        "No point keeping assignments or study material past the deadline.",
        "https://st2.depositphotos.com/1004274/8297/v/450/depositphotos_82971018-stock-illustration-check-list-icon-business-concept.jpg",
    ),
    Blurred(
        "Blurred, Shaky or Low Quality Images",
        "Blurred images can take up loads of space while having low quality.",
        "http://angularscript.com/wp-content/uploads/2018/06/Progressively-Loading-Images-With-Blur-Effect-min.png",
    ),
    Chat(
        "Chat Screenshots",
        "Remember that time when you wanted to share a screenshot with your friend but never really needed it again?",
        "https://img.gadgethacks.com/img/32/66/63728437700755/0/pin-conversations-top-messages-ios-14-unpin-them-later.w1456.jpg"
    ),
    Food(
        "Food items and Dishes",
        "Images aren't really as mesmerising as the real thing are they?",
        "https://d18mqtxkrsjgmh.cloudfront.net/public/2021-03/Eating%20More%20Ultraprocessed%20%E2%80%98Junk%E2%80%99%20Food%20Linked%20to%20Higher%20CVD%20Risk.jpeg"
    ),
    GoodWish(
        "GoodWishes and Quotes",
        "Any morning would feel lack luster without a good morning WhatsApp forward but it's clogging up your storage.",
        "https://cdn.dribbble.com/userupload/2608992/file/original-02e424ce013e25d3601879ba3dee12c6.png?resize=400x0"
    ),
    Meme(
        "Memes and Funny Images",
        "Not really doing your phone any favours after you've had your couple laughs.",
        "https://play-lh.googleusercontent.com/xlnwmXFvzc9Avfl1ppJVURc7f3WynHvlA749D1lPjT-_bxycZIj3mODkNV_GfIKOYJmG"
    ),
    Notice(
        "Notices and Alerts",
        "These notices and alerts get outdated and useless pretty fast.",
        "https://cdn1.vectorstock.com/i/thumb-large/45/85/notice-graphic-icon-vector-16744585.jpg",
    ),
    Other("Others", "Useful Images", "Error, This Should not be displayed!"),
    Pet(
        "Pets and Animals",
        "We all love hoarding cute images of pets being derpy but they can often outstay their welcome.",
        "https://americanpetsalive.org/uploads/blog/Healthy-Kittens.jpg",
    ),
}
