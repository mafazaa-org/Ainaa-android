package com.example.protectme

import android.content.Context
import java.util.*

object SecurityTips {

    private val tips = listOf(
        "تجنب استخدام شبكات الواي فاي العامة بدون تفعيل الحماية",
        "قم بتحديث التطبيق بانتظام للحصول على أحدث قوائم الحظر",
        "استخدم كلمات مرور قوية تحتوي على أحرف وأرقام ورموز",
        "تفعيل المصادقة الثنائية يضيف طبقة حماية إضافية",
        "احذر من النقر على روابط غير معروفة في الرسائل الإلكترونية",
        "تأكد دائماً من أن الموقع يستخدم HTTPS قبل إدخال بياناتك",
        "قم بعمل نسخ احتياطي دوري لبياناتك المهمة"
    )

    private var lastTipIndex = -1

    // دالة محسنة لمنع تكرار النصائح
    fun getDailyTip(): String {
        var randomIndex = Random().nextInt(tips.size)
        while (randomIndex == lastTipIndex && tips.size > 1) {
            randomIndex = Random().nextInt(tips.size)
        }
        lastTipIndex = randomIndex
        return tips[randomIndex]
    }

    // دالة لإضافة نصائح جديدة ديناميكياً
    fun addCustomTip(newTip: String) {
        if (!tips.contains(newTip)) {
            tips.toMutableList().add(newTip)
        }
    }
    // قائمة بالنصائح الأمنية
    private val securityTipsList = listOf(
        "تجنب استخدام شبكات الواي فاي العامة بدون تفعيل الحماية",
        "قم بتحديث التطبيق بانتظام للحصول على أحدث قوائم الحظر",
        "لا تشارك معلوماتك الشخصية على المواقع غير الموثوقة",
        "استخدم كلمات مرور قوية ومختلفة لكل حساب",
        "تفقد أذونات التطبيقات بانتظام وأزل غير الضرورية"
    )

    // قائمة محاكاة للمواقع المحظورة (يمكن استبدالها بقاعدة بيانات حقيقية)
    private val blockedSites = listOf(
        "example1.com",
        "example2.com",
        "example3.com",
        // يمكن إضافة المزيد من المواقع
    )

    /**
     * الحصول على عدد المواقع المحظورة
     */
    fun getBlockedSitesCount(): Int {
        // في التطبيق الحقيقي، يمكن جلب هذا الرقم من قاعدة بيانات أو API
        return blockedSites.size + (1000..5000).random()
    }

    /**
     * الحصول على قائمة بالمواقع المحظورة
     */
    fun getBlockedSites(): List<String> {
        return blockedSites
    }

    /**
     * الحصول على نصيحة أمنية عشوائية
     */
    fun getRandomSecurityTip(): String {
        return securityTipsList.random()
    }

    /**
     * التحقق مما إذا كان الموقع محظوراً
     */
    fun isSiteBlocked(url: String): Boolean {
        return blockedSites.any { url.contains(it, ignoreCase = true) }
    }

    /**
     * الحصول على آخر تاريخ تحديث لقوائم الحظر
     */
    fun getLastUpdateDate(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
    }

    /**
     * الحصول على إحصاءات الحماية
     */
    fun getProtectionStats(): Map<String, Any> {
        return mapOf(
            "blockedSitesCount" to getBlockedSitesCount(),
            "lastUpdate" to getLastUpdateDate(),
            "activeProtection" to true
        )
    }

    /**
     * إضافة موقع جديد للقائمة السوداء
     */
    fun addToBlockedSites(url: String) {
        // في التطبيق الحقيقي، هنا يتم حفظ الموقع في قاعدة البيانات
        if (!blockedSites.contains(url)) {
            blockedSites.toMutableList().add(url)
        }
    }

    /**
     * الحصول على معلومات حول مستوى الحماية
     */
    fun getProtectionLevel(context: Context): String {
        return when {
            getBlockedSitesCount() > 3000 -> "عالي جداً"
            getBlockedSitesCount() > 1000 -> "عالي"
            else -> "متوسط"
        }
    }
}
