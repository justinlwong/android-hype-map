package justin.apackage.com.hypemap.model

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

/**
 * An utils object to perform parsing data from Instagram endpoints
 *
 * @author Justin Wong
 */
object Parser {
    private const val TAG = "Parser"

    @JvmStatic
    fun getUserWrapper(response: String): UserWrapper {
        val postsList = mutableListOf<RawPost>()

        val obj = JSONObject(response)
        val userJson: JSONObject = obj.getJSONObject("graphql")
            .getJSONObject("user")

        val userName = userJson.getString("username")

        val user = User(
            userName = userName,
            profilePicUrl = userJson.getString("profile_pic_url"),
            visible = true,
            colour = ((0..11).shuffled().first() * 30).toFloat()
        )

        val posts: JSONArray = userJson.getJSONObject("edge_owner_to_timeline_media")
            .getJSONArray("edges")

        for (i in 0..(posts.length() - 1)) {
            val node = posts.getJSONObject(i)
                .getJSONObject("node")

            if (!node.isNull("location")) {
                val location = node.getJSONObject("location")

                Log.d(TAG, "location: ${location.getString("name")} id: " +
                        location.getString("id"))

                var captionText = ""
                val captionEdge = posts.getJSONObject(i)
                    .getJSONObject("node")
                    .getJSONObject("edge_media_to_caption")

                if (!captionEdge.isNull("edges")) {
                    captionText = captionEdge.getJSONArray("edges")
                        .getJSONObject(0)
                        .getJSONObject("node")
                        .getString("text")
                }

                val postId = node.getString("id")
                val newPost = RawPost(
                    id = postId,
                    userName = userName,
                    locationName = location.getString("name"),
                    locationId = location.getString("id"),
                    postUrl = node
                        .getString("thumbnail_src"),
                    linkUrl = "https://www.instagram.com/p/${node
                        .getString("shortcode")}",
                    caption = captionText,
                    timestamp = node.getLong("taken_at_timestamp"))
                postsList.add(newPost)
            }
        }

        return UserWrapper(user, postsList)
    }

    @JvmStatic
    fun getLocation(post: RawPost, response: String?): PostLocation? {
        if (response != null) {
            val jsonObj = JSONObject(response)

            val locationData = jsonObj.getJSONObject("graphql")
                .getJSONObject("location")

            val latitude = locationData.getDouble("lat")
            val longitude = locationData.getDouble("lng")
            val id: String = locationData.getString("id")

            Log.d(TAG, "location $id mapped to $latitude, $longitude")
            return PostLocation(id,
                post.userName,
                post.locationName,
                post.locationId,
                latitude,
                longitude,
                post.postUrl,
                post.linkUrl,
                post.caption,
                post.timestamp,
                true)
        } else {
            Log.d(TAG, "Bad location response")
        }
        return null
    }
}