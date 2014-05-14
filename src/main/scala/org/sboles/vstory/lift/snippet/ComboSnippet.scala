
package org.sboles.vstory.lift {

  package snippet {

    import _root_.scala.collection.mutable.Queue
    import _root_.scala.xml.NodeSeq

    import _root_.net.liftweb.common.Logger

    import org.sboles.vstory.lift.lib.datatype.ComboData
    import org.sboles.vstory.lift.lib.datatype.ItemData

    object ComboSnippet {

      private val logger = Logger(classOf[ComboSnippet])

      def itemToHtml(item: ItemData): NodeSeq = {
        <span>
        <span class="itemMaterialName">{item.material.name}</span>
        <span class="itemName">{item.name}</span>
        <span class="itemId">({item.id})</span>
        <span class="itemTypeName">(item.itemType.name)</span>
        </span>
      }

      /**
       * @return HTML nodes containing summary
       */
      def toSummary(combo: ComboData): NodeSeq = {
        combo.result match {
          case None => {
            combo.swap match {
              case None => {
                // no result, no swap
                <span>
                { itemToHtml(combo.item1) }
                <span>and</span>
                { itemToHtml(combo.item2) }
                <span>do not create anything</span>
                </span>
              }
              case Some(s) => {
                // no result, swap
                <div>
                { itemToHtml(combo.item1) }
                <span>and</span>
                { itemToHtml(combo.item2) }
                <span>do not create anything.</span>
                <div style="margin-left:16px; font-style:italic;">If swapped,</div>
                { itemToHtml(combo.item2) }
                <span>and</span>
                { itemToHtml(combo.item1) }
                <span>create</span>
                { itemToHtml(s) }
                </div>
              }
            }
          }
          case Some(r) => {
            combo.swap match {
              case None => {
                // result, no swap
                <div>
                { itemToHtml(combo.item1) }
                <span>and</span>
                { itemToHtml(combo.item2) }
                <span>create</span>
                { itemToHtml(r) }
                </div>
              }
              case Some(s) => {
                // result, swap
                <div>
                { itemToHtml(combo.item1) }
                <span>and</span>
                { itemToHtml(combo.item2) }
                <span>create</span>
                { itemToHtml(r) }
                <div style="margin-left:16px; font-style:italic;">If swapped,</div>
                { itemToHtml(combo.item2) }
                <span>and</span>
                { itemToHtml(combo.item1) }
                <span>create</span>
                { itemToHtml(s) }
                </div>
              }
            }
          }
        }
      }

      def toResult(combo: ComboData): NodeSeq = {
        combo.result match {
          case None => <span class="resultNone">No result</span>
          case Some(r) => itemToHtml(r)
        }
      }
    }

    class ComboSnippet { }
  }
}
