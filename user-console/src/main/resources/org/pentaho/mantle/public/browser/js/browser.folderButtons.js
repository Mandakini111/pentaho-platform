/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


define([
  "common-ui/jquery-pentaho-i18n",
  "common-ui/jquery"
], function () {

  var local = {
    renameDialog: null,

    init: function () {
      var that = this; // trap this

      // initialize buttons definitions
      that.buttons = [
        {
          id: "newFolderButton",
          text: this.i18n.prop('contextAction_newFolder'),
          handler: $.proxy(that.newFolderHandler, that)
        },
        {
          id: "deleteFolderButton",
          text: this.i18n.prop('contextAction_delete'),
          handler: $.proxy(that.deleteFolderHandler, that)
        },
        {
          id: "renameButton",
          text: this.i18n.prop('contextAction_rename'),
          handler: $.proxy(that.renameHandler, that)
        },
        {id: "separator"},
        {
          id: "pasteButton",
          text: this.i18n.prop('contextAction_paste'),
          handler: $.proxy(that.pasteHandler, that)
        },
        {id: "separator"},
        {
          id: "uploadButton",
          text: this.i18n.prop('contextAction_upload'),
          handler: $.proxy(that.uploadHandler, that)
        },
        {
          id: "downloadButton",
          text: this.i18n.prop('contextAction_download'),
          handler: $.proxy(that.downloadHandler, that)
        },
        {id: "optional-separator"},
        {
          id: "propertiesButton",
          text: this.i18n.prop('contextAction_properties'),
          handler: $.proxy(that.propertiesHandler, that)
        }
      ];

      that.initEventHandlers();

    },

    buttons: [],

    initEventHandlers: function () {
      // listen for file action events
      if (window.parent.mantle_addHandler) {
        window.parent.mantle_addHandler("SolutionFolderActionEvent", this.eventLogger);
        window.parent.mantle_addHandler("SolutionFileActionEvent", this.postPropertiesHandler);
      }
    },

    buildParameter: function (path, title) {
      return {
        solutionPath: (path == null ? "/" : path ),
        fileNames: (title ? null : title)
      };
    },

    urlParam: function (paramName) {
      var value = new RegExp('[\\?&]' + paramName + '=([^&#]*)').exec(window.parent.location.href);
      if (value) {
        return value[1];
      }
      else {
        return null;
      }
    },

    canPublish: function (canPublish) {
      if (canPublish) {
        $('#uploadButton').show();
      }
      else {
        $('#uploadButton').hide();
      }
      if ( ( $('#downloadButton').css('display') == "inline-block" ) ||
          ( $('#uploadButton').css('display') == "inline-block" ) ) {
        $('#optional-separator').show()
      }
      else {
        $('#optional-separator').hide()
      }
    },

    canDownload: function (canDownload) {
      if (canDownload) {
        $('#downloadButton').show();
      }
      else {
        $('#downloadButton').hide();
      }
      if ( ( $('#downloadButton').css('display') == "inline-block" ) ||
          ( $('#uploadButton').css('display') == "inline-block" ) ) {
        $('#optional-separator').show()
      }
      else {
        $('#optional-separator').hide()
      }
    },

    updateFolderPermissionButtons: function (permissions, multiSelectItems, renameAllowed) {
      if (permissions != false) {

        var writePerm = "false";
        for (var i = 0; i < permissions.setting.length; i++) {
          //Write Permission
          if (permissions.setting[i].name == "1") {
            writePerm = permissions.setting[i].value;
          }
        }

        if (writePerm == "true") {
          var disabled = (multiSelectItems.length > 0) ? false : true;
          $("#pasteButton").prop("disabled", disabled);
          $("#renameButton").prop("disabled", !renameAllowed);
        } else {
          $("#pasteButton").prop("disabled", true);
          $("#renameButton").prop("disabled", true);
        }
      }
    },

    enableButtons: function (enableButtons) {
      this.buttons.forEach((buttonDef) => {
        let target;
        if (buttonDef.id == "separator") {
          target = $(".separator");
        } else {
          target = $("#" + buttonDef.id);
        }

        target.prop("disabled", !enableButtons);

        if (!enableButtons) {
          target.hide();
        } else {
          target.show();
        }
      });
    },

    eventLogger: function (event) {
      console.log(event.action + " : " + event.message);
    },

    newFolderHandler: function (path) {
      if (window.parent != undefined)
        window.parent.executeCommand("NewFolderCommand", this.buildParameter(path));
    },

    deleteFolderHandler: function (path) {
      window.parent.executeCommand("DeleteFolderCommand", this.buildParameter(path));
    },

    pasteHandler: function (path, title, id,  multiSelectItems, browserUtils) {
      if (browserUtils.multiSelectItems.length > 0 ) {
        window.parent.executeCommand("PasteFilesCommand", this.buildParameter(path));
      }
    },

    uploadHandler: function (path) {
      window.parent.executeCommand("ImportFileCommand", this.buildParameter(path));
    },

    downloadHandler: function (path) {
      window.parent.executeCommand("ExportFileCommand", this.buildParameter(path));
    },

    propertiesHandler: function (path) {
      window.parent.executeCommand("FilePropertiesCommand", this.buildParameter(path));
    },

    postPropertiesHandler: function () {
      $(window.parent.document).find(".pentaho-dialog:not(.modal)").attr("id", "browse-properties-dialog");
    },
    renameHandler: function (path) {
      this.renameDialog.init(path, "folder");
    }

  };

  var FolderButtons = function (i18n) {
    this.i18n = i18n;
    this.init();
  }
  FolderButtons.prototype = local;
  return FolderButtons;
});
