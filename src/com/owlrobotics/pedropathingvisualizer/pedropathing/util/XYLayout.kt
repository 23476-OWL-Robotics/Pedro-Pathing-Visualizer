package com.owlrobotics.pedropathingvisualizer.pedropathing.util

import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager

class XYLayout : LayoutManager {
    override fun addLayoutComponent(name: String, comp: Component) {
    }

    override fun removeLayoutComponent(comp: Component) {
    }

    override fun preferredLayoutSize(parent: Container): Dimension? {
        return null
    }

    override fun minimumLayoutSize(parent: Container): Dimension? {
        return null
    }

    override fun layoutContainer(parent: Container) {
        val nComps = parent.componentCount

        for (i in 0 until nComps) {
            val c = parent.getComponent(i)

            c.size = c.preferredSize
        }
    }
}
