package io.github.grishaninvyacheslav.con_tac_tix

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.terrakok.cicerone.androidx.AppNavigator
import io.github.grishaninvyacheslav.con_tac_tix.databinding.ActivityMainBinding
import io.github.grishaninvyacheslav.con_tac_tix.ui.BackButtonListener
import io.github.grishaninvyacheslav.con_tac_tix.presenters.main.MainPresenter
import io.github.grishaninvyacheslav.con_tac_tix.presenters.main.MainView
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class MainActivity : MvpAppCompatActivity(), MainView {
    private val navigator = AppNavigator(this, R.id.container)

    private val presenter by moxyPresenter {
        MainPresenter()
    }
    private val view: ActivityMainBinding by viewBinding(createMethod = CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        App.instance.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        App.instance.navigatorHolder.removeNavigator()
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it is BackButtonListener && it.backPressed()) {
                return
            }
        }
        presenter.backClicked()
    }
}
