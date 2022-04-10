package com.ddplay.yuntech.Common

import android.graphics.RectF
import com.ddplay.yuntech.R

class CommonData {
    /*-------------------------------------------------------------------*/
    val url = "http://192.168.50.91:3000/"
    // upload url
    val detailUrl = "http://192.168.50.91:3000/upload/add"
    val imageUrl = "http://192.168.50.91:3000/upload/image"
    /*-------------------------------------------------------------------*/
    // My Location
    data class MyLocation(val longitude: Double, val latitude: Double)
    /*-------------------------------------------------------------------*/
    // History Item
    data class History(val variety: String, val Img: ByteArray,
                       val Time: String, val Lat: String, val Lng: String) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as History

            if (variety != other.variety) return false
            if (!Img.contentEquals(other.Img)) return false
            if (Time != other.Time) return false
            if (Lat != other.Lat) return false
            if (Lng != other.Lng) return false

            return true
        }
        override fun hashCode(): Int {
            var result = variety.hashCode()
            result = 31 * result + Img.contentHashCode()
            result = 31 * result + Time.hashCode()
            result = 31 * result + Lat.hashCode()
            result = 31 * result + Lng.hashCode()
            return result
        }
    }
    /*-------------------------------------------------------------------*/
    // Object Detection boundingBox and name
    data class DetectionResult(val boundingBox: RectF, val text: String)
    /*-------------------------------------------------------------------*/
    // Category Library
    val map = mapOf(
        "Black drongo"              to "大卷尾",
        "Black-crowned Night-Heron" to "夜鷺",
        "Crested Myna"              to "八哥",
        "Duck"                      to "鴨子",
        "Intermediate Egret"        to "中白鷺",
        "Malayan night heron"       to "黑冠麻鷺",
        "Mallard"                   to "綠頭鴨",
        "Pigeon"                    to "鴿子",
        "Red-bellied Squirrel"      to "赤腹松鼠",
        "Sparrow"                   to "麻雀",
        "Taiwan Dog"                to "小狗"
    )
    /*-------------------------------------------------------------------*/
    // Information Item
    data class Information(val img: Int, val Name: String, val Content: String)
    val infoImg = intArrayOf(R.drawable.pigeon, R.drawable.malayan_night_heron,
                             R.drawable.black_crowned_night_heron, R.drawable.sparrow,
                             R.drawable.mallard, R.drawable.black_drongo,
                             R.drawable.intermediate_egret, R.drawable.crested_myna,
                             R.drawable.dog, R.drawable.red_bellied_squirrel,
                             R.drawable.duck)
    val infoName = arrayOf("野鴿", "黑冠麻鷺", "夜鷺", "麻雀", "綠頭鴨", "大卷尾", "中白鷺", "八哥",
                           "雲科狗狗", "赤腹松鼠", "家鴨")
    val infoContent = arrayOf(
        "野鴿（學名：Columba livia），又名野鴿子、原鴿、鵓鴿。為人類所馴化的野鴿被稱為家鴿。分布於印度次大陸" +
        "的部分地區、古北界南部，引種至世界各地，如今許多城鎮都有野化的鴿群。在中國西北部及喜馬拉雅山脈。",

        "黑冠麻鷺（學名：Gorsachius melanolophus）又名黑冠鳽或黑冠虎斑鳽，臺灣話俗稱山暗光，亦有地瓜鳥和大" +
        "笨鳥的別稱，是中型的鷺科鳥類，主要分布於季風亞洲的東南部至南部，其中大部分地區的族群是稀有留鳥，只有臺" +
        "灣的族群在近年成為常見的留鳥，另有少數族群會在中國華南、斯里蘭卡與海洋東南亞之間遷徙。",

        "夜鷺（學名：Nycticorax nycticorax）又稱黑頂夜鷺，俗稱灰窪子、夜窪子、星雁、暗光鳥仔，是鷺科夜鷺屬" +
        "的一種。夜鷺是分布非常廣泛的一種鳥類，分布遍及歐亞大陸、非洲，整個美洲大陸及東南亞等地。夜鷺在台灣為常" +
        "見的留鳥，由於性喜食魚，魚類養殖業者視其為害鳥。",

        "麻雀（學名：Passer montanus）又名樹麻雀、霍雀、嘉賓、瓦雀、琉雀、家雀、老家子、老家賊、照夜、麻谷、" +
        "南麻雀、賓雀、厝鳥、砉鶉；屋角鳥、屋簷鳥，是麻雀屬下的一種鳥類。麻雀廣泛分布於歐亞大陸，歐洲、中亞、東" +
        "南亞、東亞均可見到此物種。它們普遍在樹上築巢，身形比起其他鳥類來説比較小，身體大部分為褐色與白色，且相" +
        "較其它鳥類特別的是，麻雀在陸地上移動時是用跳的。",

        "綠頭鴨（學名：Anas platyrhynchos）又名大頭綠（雄）、蒲鴨（雌）、野鴨，古稱鶩[註 1]，家鴨是其馴化亞" +
        "種。綠頭鴨飛行速度可達到每小時65公里。綠頭鴨是台灣常見的雁鴨科鳥類，主要分布於北半球，在台灣雖是冬候鳥" +
        "，但也有不少的留鳥，人工飼養的歷史相當悠久，有些觀光地區的湖泊、水塘等也會飼養供觀賞。",

        "大卷尾（學名：Dicrurus macrocercus，英語：Black drongo）為卷尾科卷尾屬的一種，又名黑卷尾，俗名吃" +
        "杯茶、鐵煉甲、籬雞、鐵燕子、黑黎雞。在台灣俗稱烏鶖或烏秋。分布於西自伊朗、阿富汗向東南至南亞和東南亞、俄" +
        "羅斯、台灣以及中國大陸的黑龍江、吉林、遼寧、內蒙古、華北各省、西至陝西、四川、西藏、長江以南流域地區、西" +
        "抵雲南、海南等地，常見於城郊村莊附近和廣大農村以及尤喜在村民屋前後高大的椿樹上營巢。",

        "中白鷺（學名：Ardea Intermedia）為鷺科蒼鷺屬的鳥類，俗名春鋤。分布於非洲、印度、孟加拉國、緬甸、馬來" +
        "半島、菲律賓、越南至日本、巽他群島、馬魯古群島、澳大利亞以及中國大陸的中國南部、西至陝西、甘肅、四川、雲" +
        "南、貴州、北至河南為夏候鳥、廣東、海南、台灣為冬候鳥、偶見於北京等地，多生活於稻田、湖泊、沼澤及灘涂、築" +
        "巢於村寨附近的喬木和竹林上以及在貴州分布於海拔400-800m。",

        "八哥（學名：Acridotheres cristatellus），別名有鸚鵒、寒皋、鴝鵒、鸜鵒、蒼鵒、駕鴒、加令、中國鳳頭八" +
        "哥、鳳頭八哥、沉香色八哥、了哥，古時稱秦吉了，是八哥屬的一種鳥類。八哥生活在草原和山區的樹林中，善於鳴叫" +
        "，也會模仿其他鳥的叫聲，經過訓練，還能模仿人類說話。",

        "在雲科校內及周圍活動的狗狗，由雲科旺旺社負責管理。",

        "赤腹松鼠（學名：Callosciurus erythraeus）為松鼠科麗松鼠屬的動物。分布於孟加拉國、柬埔寨、印度、寮國" +
        "、馬來西亞、緬甸、泰國、越南、台灣、西藏及中國的陝西、安徽、雲南、海南、四川、江蘇、福建、浙江、湖北等地" +
        "，主要生活於熱帶、亞熱帶森林。赤腹松鼠台北亞種（學名：Callosciurus erythraeus roberti），Bonhte" +
        "於1901年命名。在台灣，分布於台北等地。該物種的模式產地在台灣西北部。",

        "家鴨（學名：Anas platyrhynchos domesticus）是經人工馴化和飼養的綠頭鴨，是常見的家禽。經飼養的家鴨" +
        "通常會用作食用或取其羽毛作羽絨，不少家鴨亦用於表演、觀賞或當為寵物。家鴨品種很多，北京鴨，土鴨，褐色菜鴨" +
        "，和白色菜鴨等，是同一物種。"
    )
}