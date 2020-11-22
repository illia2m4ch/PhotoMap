package com.ilya.photomap.ui.base;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * onAttach() and onDetach().
 */
public class BasePresenter<T extends BaseView> {

    private T view;

    private CompositeDisposable compositeDisposable;

    public BasePresenter() {
        compositeDisposable = new CompositeDisposable();
    }

    public void attachView(T view) {
        this.view = view;
    }

    public void detachView() {
        compositeDisposable.dispose();
        view = null;
    }

    public boolean isViewAttached() {
        return getView() != null;
    }

    public T getView() {
        if (view == null) throw new IllegalStateException("View is not attached");
        return view;
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }
}
