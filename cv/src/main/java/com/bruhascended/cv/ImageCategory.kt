package com.bruhascended.cv

enum class ImageCategory(
    val fullName: String,
    val description: String,
    val thumbnail: String,
) {
    Assignment(
        "Assignments",
        "Pictures of handwritten or typed documents.",
        "https://st2.depositphotos.com/1004274/8297/v/450/depositphotos_82971018-stock-illustration-check-list-icon-business-concept.jpg",
    ),
    Blurred(
        "Low Quality",
        "Blurred, Shaky or Low Quality Images",
        "http://angularscript.com/wp-content/uploads/2018/06/Progressively-Loading-Images-With-Blur-Effect-min.png",
    ),
    Chat(
        "Chats",
        "Screenshots of chats, texts and messages.",
        "https://img.gadgethacks.com/img/32/66/63728437700755/0/pin-conversations-top-messages-ios-14-unpin-them-later.w1456.jpg"
    ),
    Food(
        "Food",
        "Captured images of food items such as dishes or fruits.",
        "https://d18mqtxkrsjgmh.cloudfront.net/public/2021-03/Eating%20More%20Ultraprocessed%20%E2%80%98Junk%E2%80%99%20Food%20Linked%20to%20Higher%20CVD%20Risk.jpeg"
    ),
    GoodWish(
        "GoodWishes",
        "Commonly forwarded messages that contain well wishes, quotes, festival wishes etc.",
        "https://play-lh.googleusercontent.com/cE52Cw-CKQnvJUodA_gKE8FFNMjPaKXn6LnoElJE-NL9vf5GNux-j6Y35pPzZD9E9sio"
    ),
    Meme(
        "Memes",
        "Funny images containing memes, green-texts, screenshots, tweets etc.",
        "https://upload.wikimedia.org/wikipedia/en/thumb/9/9a/Trollface_non-free.png/220px-Trollface_non-free.png"
    ),
    Notice(
        "Notices",
        "Documents containing notices, schedules, date-sheets etc.",
        "https://pragjyotishcollege.ac.in/wp-content/uploads/2022/01/WhatsApp-Image-2022-01-24-at-2.44.01-PM.jpeg",
    ),
    Other("Others", "Useful Images", "Error, This Should not be displayed!"),
    Pet(
        "Pets",
        "Cute animal pictures.",
        "https://americanpetsalive.org/uploads/blog/Healthy-Kittens.jpg",
    ),
}
