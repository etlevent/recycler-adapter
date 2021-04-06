package ext.android.adapter.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import ext.android.adapter.delegate.ItemViewDelegate


abstract class DataBindingItemDelegate<T, V : ViewDataBinding> :
        ItemViewDelegate<T, DataBindingViewHolder<V>> {
    override fun createViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
    ): DataBindingViewHolder<V> {
        val binding = createViewDataBinding(inflater, parent)
        return DataBindingViewHolder(binding)
    }

    override fun convert(holder: DataBindingViewHolder<V>, data: T, position: Int) {
        bind(holder.binding, data, position)
        holder.binding.executePendingBindings()
    }

    abstract fun createViewDataBinding(inflater: LayoutInflater, parent: ViewGroup): V
    abstract fun bind(binding: V, t: T, position: Int)
}

abstract class DataBindingLayoutItemDelegate<T, V : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
        DataBindingItemDelegate<T, V>() {
    override fun createViewDataBinding(inflater: LayoutInflater, parent: ViewGroup): V =
            DataBindingUtil.inflate(inflater, layoutId, parent, false)
}