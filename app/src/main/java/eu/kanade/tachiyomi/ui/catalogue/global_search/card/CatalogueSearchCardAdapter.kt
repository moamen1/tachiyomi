package eu.kanade.tachiyomi.ui.catalogue.global_search.card

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.ui.catalogue.global_search.CatalogueSearchController

/**
 * Adapter that holds the manga items from search results.
 *
 * @param controller instance of [CatalogueSearchController].
 */
class CatalogueSearchCardAdapter(controller: CatalogueSearchController) :
        FlexibleAdapter<CatalogueSearchCardItem>(null, controller, true) {

    /**
     * Listen for browse item clicks.
     */
    val mangaClickListener: OnMangaClickListener = controller

    /**
     * Listener which should be called when user clicks browse.
     * Note: Should only be handled by [CatalogueSearchController]
     */
    interface OnMangaClickListener {
        fun OnMangaClickListener(manga: Manga)
    }
}