package ru.levkopo.barsik.emu

import org.jacorb.ir.gui.typesystem.*
import org.jacorb.ir.gui.typesystem.remote.RemoteTypeSystem
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.table.JTableHeader
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

/*
*        JacORB - a free Java ORB
*
*   Copyright (C) 1997-2014 Gerald Brose / The JacORB Team.
*
*   This library is free software; you can redistribute it and/or
*   modify it under the terms of the GNU Library General Public
*   License as published by the Free Software Foundation; either
*   version 2 of the License, or (at your option) any later version.
*
*   This library is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*   Library General Public License for more details.
*
*   You should have received a copy of the GNU Library General Public
*   License along with this library; if not, write to the Free
*   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

/**
 * @author (c) Joerg von Frantzius, Gerald Brose, FU Berlin
 */
class IRBrowser

    : JFrame, WindowListener, MouseListener, ListSelectionListener, TreeSelectionListener, ActionListener {
    var contentTable: JTable? = null
    var splitPane: JSplitPane? = null
    var treeView: JTree? = null
    var textArea: JTextArea? = null
    var typeSystem: TypeSystem? = null
    var treeModel: TreeModel? = null
    var followTypeMenu: JMenuItem? = null

    /**
     * Constructor
     */
    constructor() : super() {
        try {
            typeSystem = RemoteTypeSystem()
        } catch (e: Exception) {
            System.err.println("Caught exception starting browser")
            e.printStackTrace()
        }
        initialize()
    }

    /**
     * @param repositoryIOR java.lang.String
     */
    constructor(repositoryIOR: String?) : super() {
        try {
            typeSystem = RemoteTypeSystem(repositoryIOR)
        } catch (e: Exception) {
            System.err.println("Caught exception starting browser")
            e.printStackTrace()
            System.exit(-1)
        }
        initialize()
    }

    /**
     * @param event java.awt.ActionEvent
     */
    override fun actionPerformed(event: ActionEvent) {
        val nodeMapper =
            contentTable!!.model.getValueAt(contentTable!!.selectedRow, 0) as NodeMapper
        val typeSystemNode = nodeMapper.node
        followTypeOf(typeSystemNode)
        println("following type of $typeSystemNode")
    }

    /**
     * conn0:  (IRBrowser.window.windowClosing(java.awt.event.WindowEvent) --> IRBrowser.dispose())
     * @param arg1 java.awt.event.WindowEvent
     */
    private fun conn0(arg1: WindowEvent) {
        try {
            // user code begin {1}
            // user code end
            this.dispose()
            // user code begin {2}
            // user code end
        } catch (ivjExc: Throwable) {
            // user code begin {3}
            // user code end
            handleException(ivjExc)
        }
    }

    /**
     * @param typeSystemNode typesystem.TypeSystemNode
     */
    fun followTypeOf(typeSystemNode: TypeSystemNode?) {
        var treeNode: DefaultMutableTreeNode? = null

        if (typeSystemNode is TypeAssociator) {
            val assTypeNode =
                (typeSystemNode as TypeAssociator).associatedTypeSystemNode
            if (assTypeNode.getModelRepresentant(treeModel) != null) {
                treeNode =
                    assTypeNode.getModelRepresentant(treeModel) as DefaultMutableTreeNode
            }
        }
        if (treeNode != null) {
            // If Node is an AbstractContainer or has an associated
            // TypeSystemNode, jump to the corresponding location in treeView
            val treeModel =
                treeView!!.model as DefaultTreeModel
            val fullTreePath =
                TreePath(treeModel.getPathToRoot(treeNode))

            treeView!!.scrollPathToVisible(fullTreePath)
            // set selection to node
            treeView!!.selectionPath = fullTreePath
            treeView!!.validate()
        }
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private fun handleException(exception: Throwable) {
        exception.printStackTrace()
    }

    /**
     * Initializes connections
     */
    private fun initConnections() {
        this.addWindowListener(this)
    }


    /**
     * Initialize class
     */
    fun initialize() {
        //	setBackground(java.awt.Color.lightGray);
        title = Companion.title

        splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)

        // Create the table
        val tableModel = typeSystem!!.getTableModel(null)
        contentTable = JTable(tableModel)
        contentTable!!.autoCreateColumnsFromModel = true
        //	contentTable.setModel(tableModel);
        contentTable!!.columnSelectionAllowed = false
        contentTable!!.rowSelectionAllowed = true
        contentTable!!.cellSelectionEnabled = false
        contentTable!!.removeEditor()
        contentTable!!.setShowGrid(false)
        contentTable!!.tableHeader = JTableHeader(contentTable!!.columnModel)
        contentTable!!.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
        //    contentTable.setBackground(java.awt.Color.white);
        //	contentTable.setMinimumSize(new java.awt.Dimension(100,contentTable.getMinimumSize().height));
        contentTable!!.minimumSize = Dimension(100, 100)
        contentTable!!.selectionModel.addListSelectionListener(this)


        this.treeModel = typeSystem!!.treeModel
        //	this.treeModel = typeSystem.createTreeModelRoot();
        treeView = JTree(treeModel)
        //	treeView.addTreeExpansionListener(typeSystem.getTreeExpansionListener(this.treeModel));
        treeView!!.isRootVisible = true
        treeView!!.showsRootHandles = true
        //	treeView.setBackground(java.awt.Color.lightGray);
        //	treeView.setMinimumSize(new java.awt.Dimension(200,100));
        treeView!!.minimumSize = Dimension(100, 100)
        treeView!!.addTreeSelectionListener(this)

        val tableScrollPane = JTable.createScrollPaneForTable(contentTable)
        val treeScrollPane = JScrollPane(treeView)
        // tableScrollPane.setBackground(java.awt.Color.white);
        tableScrollPane.minimumSize = Dimension(100, 100)
        treeScrollPane.minimumSize = Dimension(100, 100)
        tableScrollPane.preferredSize = Dimension(100, 100)
        treeScrollPane.preferredSize = Dimension(100, 100)

        textArea = JTextArea("Hallo, hallo!")
        textArea!!.isEditable = false
        textArea!!.font = Font("sansserif", Font.PLAIN, 10)
        textArea!!.rows = 4
        textArea!!.tabSize = 15
        textArea!!.border = BorderFactory.createLoweredBevelBorder()
        textArea!!.minimumSize = Dimension(100, 100)
        textArea!!.preferredSize = Dimension(600, 90)

        //	textArea.setBackground(java.awt.Color.lightGray);
        splitPane!!.leftComponent = JScrollPane(treeView)
        splitPane!!.rightComponent = tableScrollPane
        //	splitPane.setRightComponent(new JScrollPane(contentTable));
        splitPane!!.dividerLocation = 300

        // Add components
        val contentPane = contentPane
        //	contentPane.setBackground(java.awt.Color.lightGray);
        /*	getContentPane().setLayout(new java.awt.BorderLayout());
                getContentPane().add(splitPane, java.awt.BorderLayout.NORTH);
                getContentPane().add(textArea, java.awt.BorderLayout.SOUTH);
        */
        //	contentPane.setLayout(new BorderLayout());
        /*	Container north = new JPanel();
                north.setLayout(new SpringLayout());
                Container south = new JPanel();
                south.setLayout(new SpringLayout());
                north.add(splitPane, SpringLayout.HEIGHT_WIDTH_SPRING);
                south.add(textArea);
                contentPane.add(north,BorderLayout.NORTH);
                contentPane.add(south,BorderLayout.SOUTH);
        */
        //	contentPane.add(splitPane,"North");
        //	contentPane.add(textArea,"South");
        splitPane!!.setBounds(0, 0, 600, 400 - textArea!!.preferredSize.height)
        textArea!!.setBounds(0, splitPane!!.bounds().height, 600, textArea!!.preferredSize.height)
        //	contentPane.setLayout(layout);
        contentPane.layout = BorderLayout()
        contentPane.add("Center", splitPane)

        //	contentPane.add(textArea);
        contentPane.add("South", textArea)
        contentPane.background = Color.white

        resize(600, 400)

        //	contentPane.setBounds();

        //	JPopupMenu popup = new JPopupMenu(treeView);
        //	popup.add(new JMenuItem("Hallo?"));
        //	popup.addPopupMenuListener(new TreePopupMenuListener());
        //	treeView.add(popup);
        val menuBar = JMenuBar()
        val menu = JMenu("Navigate")
        menuBar.add(menu)
        followTypeMenu = JMenuItem("Follow Type")
        followTypeMenu!!.isEnabled = false
        followTypeMenu!!.addActionListener(this)
        menu.add(followTypeMenu)
        jMenuBar = menuBar

        validate()
        treeView!!.expandPath(TreePath((treeModel!!.root as DefaultMutableTreeNode).path))

        treeView!!.addMouseListener(this as MouseListener)
        contentTable!!.addMouseListener(this as MouseListener)
        //	pack();
        //	validate();
        // validate();
        // user code end
        name = "IRBrowser"
        name = "IRBrowser"
        initConnections()
        // user code begin {2}
        // user code end
    }

    override fun mouseClicked(event: MouseEvent) {
        var treeNode: DefaultMutableTreeNode? = null
        // on double-click on contentTable, set treeView to the corresponding
        // TypeSystemNode
        if (event.component === contentTable && event.clickCount > 1 && contentTable!!.selectedRow != -1) {
            println("contentTable doubleClick")

            // In the TableModel there's a NodeMapper in each cell which
            // can tell us the corresponding treeNode
            val nodeMapper =
                contentTable!!.model.getValueAt(contentTable!!.selectedRow, 0) as NodeMapper
            val typeSystemNode = nodeMapper.node

            if (typeSystemNode is AbstractContainer) {
                treeNode =
                    typeSystemNode.getModelRepresentant(treeModel) as DefaultMutableTreeNode
            }

            if (typeSystemNode is TypeAssociator) {
                val assTypeNode =
                    (typeSystemNode as TypeAssociator).associatedTypeSystemNode
                if (assTypeNode.getModelRepresentant(treeModel) != null) {
                    treeNode =
                        assTypeNode.getModelRepresentant(treeModel) as DefaultMutableTreeNode
                }
            }
            if (treeNode != null) {
                // if Node is an AbstractContainer or has an associated
                // TypeSystemNode, jump to it in the treeView

                println("expanding Tree: $treeNode")
                val treeModel =
                    treeView!!.model as DefaultTreeModel
                val fullTreePath =
                    TreePath(treeModel.getPathToRoot(treeNode))

                treeView!!.scrollPathToVisible(fullTreePath)
                // Selection auf node setzen
                treeView!!.selectionPath = fullTreePath
                treeView!!.validate()
            }
        }
    }

    override fun mouseEntered(event: MouseEvent) {}
    override fun mouseExited(event: MouseEvent) {}
    override fun mousePressed(event: MouseEvent) {}
    override fun mouseReleased(event: MouseEvent) {}

    /**
     * Set the title of the Frame and enable/disable menus according
     * to the selected Node (could have been selected in TableView
     * or in TreeView).
     * @param node typesystem.TypeSystemNode
     */
    fun setSelectedNode(node: TypeSystemNode) {
        // Node could have been selected in TableView or TreeView
        title = Companion.title + " - " + node.absoluteName
        textArea!!.text = node.description()
        if (node is TypeAssociator) {
            followTypeMenu!!.isEnabled = true
        } else {
            followTypeMenu!!.isEnabled = false
        }
    }


    /**
     */
    override fun valueChanged(e: ListSelectionEvent) {
        // change contentTable only when it's a simple selection
        //	System.out.println("valueChanged (Table...)");
        var node: TypeSystemNode
        if (contentTable!!.selectedRow != -1) {
            val nodeMapper =
                contentTable!!.model.getValueAt(contentTable!!.selectedRow, 0) as NodeMapper

            if (((nodeMapper.node as TypeSystemNode).also { node = it }) != null) {
                setSelectedNode(node)
            }
        }
    }

    /**
     */
    override fun valueChanged(e: TreeSelectionEvent) {
        // change contentTable only when it's a simple selection
        val treeNode =
            e.path.lastPathComponent as DefaultMutableTreeNode

        val node =
            treeNode.userObject as TypeSystemNode

        contentTable!!.model = typeSystem!!.getTableModel(treeNode)
        contentTable!!.clearSelection()
        val tabColMod = contentTable!!.columnModel

        for (i in 0 until contentTable!!.columnCount) {
            val tabCol = tabColMod.getColumn(i)
            tabCol.cellEditor = null
            // otherwise columns would be editable
        }
        setSelectedNode(node)
        contentTable!!.validate()
        treeView!!.validate()
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */
    override fun windowActivated(e: WindowEvent) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    override fun windowClosed(e: WindowEvent) {
        // user code begin {1}
        System.exit(0)

        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    override fun windowClosing(e: WindowEvent) {
        // user code begin {1}
        // user code end
        if ((e.source === this)) {
            conn0(e)
        }
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    override fun windowDeactivated(e: WindowEvent) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    override fun windowDeiconified(e: WindowEvent) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    override fun windowIconified(e: WindowEvent) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the WindowListener interface.
     * @param e java.awt.event.WindowEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    override fun windowOpened(e: WindowEvent) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    companion object {
        private const val title = "IRBrowser"

        private fun usage() {
            println("Usage: IRBrowser [ -i ior_str | -f filename ]")
            println("    Note - if no arguments, will default to using resolve_initial_reference")
        }


        /**
         * @param args java.lang.String[]
         */
        @JvmStatic
        fun main(args: Array<String>) {
            var test: IRBrowser? = null
            var ior: String? = null

            if (args.size == 1 || args.size > 2) {
                usage()
            } else if (args.size == 2 && args[0] == "-f") {
                try {
                    val `in` =
                        BufferedReader(FileReader(args[1]))
                    ior = `in`.readLine()
                    while (ior!!.indexOf("IOR:") != 0) ior = `in`.readLine()
                    `in`.close()
                } catch (io: IOException) {
                    io.printStackTrace()
                    usage()
                }
                test = IRBrowser(ior)
            } else if (args.size == 2 && args[0] == "-i") {
                test = IRBrowser(args[1])
            } else {
                test = IRBrowser()
            }
            test!!.show()
        }
    }
}
