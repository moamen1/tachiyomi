package eu.kanade.tachiyomi.ui.catalogue.global_search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.ui.base.controller.NucleusController
import eu.kanade.tachiyomi.ui.manga.MangaController
import kotlinx.android.synthetic.main.catalogue_global_search_controller.view.*

/**
 * This controller shows and manages the different search result in global search.
 * This controller should only handle UI actions, IO actions should be done by [CatalogueSearchPresenter]
 * [CatalogueSearchCardAdapter.OnMangaClickListener] called when manga is clicked in global search
 *
 * @param query query used for global search.
 */
class CatalogueSearchController(val query: String = "") : NucleusController<CatalogueSearchPresenter>(),
CatalogueSearchCardAdapter.OnMangaClickListener{

    /**
     * Adapter containing search results grouped by lang.
     */
    private var adapter: CatalogueSearchAdapter? = null

    /**
     * Called when controller is initialized.
     */
    init {
        setHasOptionsMenu(true)
    }

    /**
     * Initiate the view with [R.layout.catalogue_global_search_controller].
     *
     * @param inflater used to load the layout xml.
     * @param container containing parent views.
     * @return inflated view
     */
    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): android.view.View {
        return inflater.inflate(R.layout.catalogue_global_search_controller, container, false)
    }

    /**
     * Set  the title of controller.
     *
     * @return title.
     */
    override fun getTitle(): String? {
        return applicationContext?.getString(R.string.action_global_search)
    }

    /**
     * Create the [CatalogueSearchPresenter] used in controller.
     *
     * @return instance of [CatalogueSearchPresenter]
     */
    override fun createPresenter(): CatalogueSearchPresenter {
        return CatalogueSearchPresenter()
    }

    /**
     * Called when manga in global search is clicked, opens manga.
     *
     * @param manga clicked item containing manga information.
     */
    override fun onMangaClick(manga: Manga) {
        // Open MangaController.
        router.pushController(RouterTransaction.with(MangaController(manga, true))
                .pushChangeHandler(FadeChangeHandler())
                .popChangeHandler(FadeChangeHandler()))
    }

    /**
     * Adds items to the options menu.
     *
     * @param menu menu containing options.
     * @param inflater used to load the menu xml.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate menu.
        inflater.inflate(R.menu.catalogue_new_list, menu)

        // Initialize search menu
        menu.findItem(R.id.action_search).apply {
            val searchView = actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    presenter.getSearchResults(query)
                    collapseActionView()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // TODO suggestions?
                    return true
                }
            })
        }
    }

    /**
     * Called when the view is created
     *
     * @param view view of controller
     * @param savedViewState information from previous state.
     */
    override fun onViewCreated(view: View, savedViewState: Bundle?) {
        super.onViewCreated(view, savedViewState)

        adapter = CatalogueSearchAdapter(this)

        with(view) {
            // Create recycler and set adapter.
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter
        }

        presenter.getSearchResults(query)
    }

    override fun onDestroyView(view: View) {
        adapter = null

        super.onDestroyView(view)
    }

    /**
     * Returns the view holder for the given manga.
     *
     * @param source used to find holder containing source
     * @return the holder of the manga or null if it's not bound.
     */
    private fun getHolder(source: CatalogueSource): CatalogueSearchHolder? {
        val adapter = adapter ?: return null

        adapter.allBoundViewHolders.forEach { holder ->
            val item = adapter.getItem(holder.adapterPosition)
            if (item != null && source.id == item.source.id) {
                return holder as CatalogueSearchHolder
            }
        }

        return null
    }

    /**
     * Add search result to adapter.
     *
     * @param searchResult result of search.
     */
    fun setItems(searchResult: List<CatalogueSearchItem>) {
        adapter?.updateDataSet(searchResult)
    }

    /**
     * Called from the presenter when a manga is initialized.
     *
     * @param manga the initialized manga.
     */
    fun onMangaInitialized(source: CatalogueSource, manga: Manga) {
        getHolder(source)?.setImage(manga)
    }

}