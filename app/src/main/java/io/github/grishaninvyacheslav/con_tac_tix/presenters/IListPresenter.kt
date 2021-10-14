package io.github.grishaninvyacheslav.con_tac_tix.presenters

interface IListPresenter<V : IItemView> {
    var itemClickListener: ((V) -> Unit)?
    fun bindView(view: V)
    fun getCount(): Int
}