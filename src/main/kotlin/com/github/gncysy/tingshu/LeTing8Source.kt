package com.github.gncysy.tingshu

import com.github.eprendre.tingshu.core.TingShu
import com.github.eprendre.tingshu.pojo.Book
import com.github.eprendre.tingshu.pojo.Category
import com.github.eprendre.tingshu.pojo.CategoryMenu
import com.github.eprendre.tingshu.pojo.CategoryTab
import com.github.eprendre.tingshu.pojo.Episode
import org.jsoup.Jsoup
import java.net.URLEncoder

class LeTing8Source : TingShu() {
    
    override fun getSourceName(): String {
        return "乐听吧"
    }
    
    override fun getDesc(): String {
        return "在线听书 - leting8.com"
    }
    
    override fun getCategoryMenus(): List<CategoryMenu> {
        val list = listOf(
            "玄幻" to "xuanhuan",
            "言情" to "yanqing",
            "都市" to "dushi"
        )
        return list.map { (name, code) ->
            CategoryMenu(name, listOf(
                CategoryTab("最新", "https://www.leting8.com/$code/"),
                CategoryTab("热门", "https://www.leting8.com/$code/hot/")
            ))
        }
    }
    
    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        val books = doc.select("div.item").map { item ->
            val a = item.selectFirst("h3 a")!!
            Book(
                coverUrl = item.selectFirst("img")?.attr("src") ?: "",
                title = a.text(),
                author = item.selectFirst(".author")?.text() ?: "",
                intro = item.selectFirst(".desc")?.text() ?: "",
                bookUrl = "https://www.leting8.com" + a.attr("href")
            )
        }
        val next = doc.selectFirst("a.next")?.attr("href") ?: ""
        return Category(books, if (next.isNotEmpty()) "https://www.leting8.com$next" else "")
    }
    
    override fun search(keywords: String, page: Int): List<Book> {
        val url = "https://www.leting8.com/search.php?q=${URLEncoder.encode(keywords, "UTF-8")}&page=$page"
        return getCategoryList(url).list
    }
    
    override fun getBookDetailInfo(bookUrl: String): Book {
        val doc = Jsoup.connect(bookUrl).userAgent("Mozilla/5.0").get()
        val episodes = doc.select(".chapter-list a").mapIndexed { i, a ->
            Episode(i, a.text(), "https://www.leting8.com" + a.attr("href"))
        }
        return Book(
            title = doc.selectFirst("h1")?.text() ?: "",
            coverUrl = doc.selectFirst(".book-img img")?.attr("src") ?: "",
            author = doc.selectFirst(".info:contains(作者)")?.text()?.replace("作者：", "") ?: "",
            intro = doc.selectFirst(".book-desc")?.text() ?: "",
            episodes = episodes,
            bookUrl = bookUrl
        )
    }
    
    override fun getAudioUrlExtractor() = com.github.eprendre.tingshu.core.AudioUrlExtractor { url ->
        val doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        doc.selectFirst("audio source")?.attr("src") ?: doc.selectFirst("[data-audio]")?.attr("data-audio") ?: ""
    }
    
    override fun isDiscoverable(): Boolean = true
    override fun isSearchable(): Boolean = true
}

