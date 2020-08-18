package com.example.moviemviimpl.utils

object Constants {
    const val CURRENT_SCROLL_POSITION = "CURRENT_SCROLL_POSITION"
    const val CURRENT_MOVIE_ID = "CURRENT_MOVIE_ID"
    const val VIEW_PAGER_POSITION = "VIEW_PAGER_POSITION"

    //TODO:  REMOVE APIS FROM HERE
    const val YOUTUBE_API_KEY = "AIzaSyD1fJgr6g67YsqoNFHRE8k6ay1kNBV6FcU"
    const val API_KEY = "b4a7db64c37af41e1d59907f71f24de4"
    const val BASE_URL = "https://api.themoviedb.org"
    const val POPULAR_MOVIES_URL = "3/movie/popular"

    const val NETWORK_TIMEOUT = 6000L
    const val CACHE_TIMEOUT = 2000L
    const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
    const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing


    const val PAGINATION_PAGE_SIZE = 10

    const val GALLERY_REQUEST_CODE = 201
    const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
    const val CROP_IMAGE_INTENT_CODE: Int = 401

    const val ORDER_BY_TITLE = "order by title"
    const val ORDER_BY_YEAR = "order by year"

    //TODO: Move to constants
    const val LOCATION_PREMMISION_CODE = 1010

}
