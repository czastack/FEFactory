package cza.app

import android.content.Context

/**
 * Created by an on 17-2-8.
 */
class ConfirmDialog(val id: Int, ctx: Context) : Dialog(ctx) {
    init {
        setConfirm()
    }

    constructor (id: Int, ctx: Context, title: String, msg: String) : this(id, ctx) {
        setTitle(title)
        setMessage(msg)
    }

    constructor (id: Int, ctx: Context, title: Int, msg: Int) : this(id, ctx) {
        setTitle(title)
        setMessage(msg)
    }
}