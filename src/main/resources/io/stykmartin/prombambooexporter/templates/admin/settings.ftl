<html>
<head>
    <meta name="decorator" content="atl.admin">
    <meta name="activeTab" content="prom-bamboo-exporter-configuration">
    <title>${action.getText('io.stykmartin.prombambooexporter.admin.settings.title')}</title>
</head>
<body>
<section id="content" role="main">
    <header class="aui-page-header">
        <div class="aui-page-header-inner">
            <div class="aui-page-header-main">
                <h1>${action.getText('io.stykmartin.prombambooexporter.admin.settings.title')}</h1>
                <p>${action.getText('io.stykmartin.prombambooexporter.admin.settings.desc')}</p>
                <p>
                    <span class="aui-icon aui-icon-small aui-iconfont-info"></span>
                    ${action.getText('io.stykmartin.prombambooexporter.admin.settings.linkdesc')}
                    <a target="_blank" href="${baseurl}/plugins/servlet/prometheus/metrics">
                        ${action.getText('io.stykmartin.prombambooexporter.admin.settings.link')}
                    </a>.
                </p>
            </div>
        </div>
    </header>
    <div class="aui-page-panel">
        <div class="aui-page-panel-inner">
            <section class="aui-page-panel-content">
                [#if saved]
                <div class="aui-message closeable shadowed">
                    <p class="title">
                        <span class="aui-icon icon-success"></span>
                        <strong>${action.getText('io.stykmartin.prombambooexporter.admin.settings.success')}</strong>
                    </p>
                </div>
                [/#if]
                [#if action.tokenConfigured]
                <div class="aui-message aui-message-info">
                    <p>${action.getText('io.stykmartin.prombambooexporter.admin.settings.status.configured')}</p>
                </div>
                [#else]
                <div class="aui-message aui-message-warning">
                    <p>${action.getText('io.stykmartin.prombambooexporter.admin.settings.status.open')}</p>
                </div>
                [/#if]
                <div id="base-form">
                    [@ww.form
                        action="savesettings"
                        id="saveSettingsForm"
                        submitLabelKey='io.stykmartin.prombambooexporter.admin.settings.actions.save'
                    ]
                        [@ww.password
                            labelKey="io.stykmartin.prombambooexporter.admin.settings.token"
                            name="token"
                            required=false
                            showPassword=true
                            descriptionKey="io.stykmartin.prombambooexporter.admin.settings.token.desc"
                        /]
                        [@ww.checkbox
                            labelKey="io.stykmartin.prombambooexporter.admin.settings.clear"
                            name="clear"
                            descriptionKey="io.stykmartin.prombambooexporter.admin.settings.clear.desc"
                        /]
                    [/@ww.form]
                </div>
            </section>
        </div>
    </div>
</section>
</body>
</html>
