if ( typeof SAPO.Voxx === 'undefined' ) { SAPO.namespace( 'Voxx' ); }
SAPO.Voxx.Webservices = function ( ws , options , params ) {
	'use strict';

	this._webservices = { };

	if ( typeof params !== 'undefined' ) {
		var _options = this._getOptions( options );
		this.executeOnce( _options , params );
	} else {
		this.addWS( ws , options );
	}
};

SAPO.Voxx.Webservices.prototype = {
	_getOptions : function( options ) {
		return SAPO.extendObj( { url : '' ,
								onComplete : function( ){ } ,
								onFailure : function( ){ } ,
								repeat : 3 ,
								timeout : 4 ,
								callbackParam : 'jsoncallback'
								} , options || { } );
	} ,

	addWS : function( ws , options ) {
		if ( typeof ws !== 'string' ) { return false; }

		this._webservices[ ws ] = this._getOptions( options );

		return this;
	} ,

	removeWS : function( ws ) {
		delete this._webservices[ ws ];

		return this;
	} ,

	updateWS : function( ws , key , value ) {
		if ( typeof ws !== 'string' ) { return false; }

		if ( typeof this._webservices[ ws ].params !== 'undefined' && typeof this._webservices[ ws ].params[ key ] !== 'undefined' ) {
			this._webservices[ ws ].params[ key ] = value;
		} else {
			this._webservices[ ws ][ key ] = value;
		}

		return this;
	} ,

	execute : function( ws , params , options ) {
		if ( typeof ws !== 'string' ) { return false; }

		var _options = SAPO.extendObj(
			this._getOptions( this._webservices[ ws ] || { } ) ,
			options || { } );

		return this.executeOnce( _options , params );
	} ,

	executeOnce : function( options , params ) {
		if ( typeof options === 'undefined' ) { return false; }

		options = this._getOptions( options );

		options.params = SAPO.extendObj(
			options.params || { },
			params || { } );

		for ( var k in options.params ) {
			if ( options.params.hasOwnProperty( k ) && !options.params[ k ] ) {
				delete options.params[ k ];
			}
		}

		options.onFailure = function( failureObj ) {
			if ( parseInt( --failureObj.repeat ) ) {
				delete this.params.jsoncallback;
				delete this.params.rnd_seed;
				delete this.internalCallback;
				delete this.randVar;

				new SAPO.Communication.JsonP( this.failureObj.url , this );
			} else {
				options.onFailure( );
			}
		};

		options.failureObj = { url : options.url ,
				repeat : options.repeat || 1
		};

		new SAPO.Communication.JsonP( options.url , options );

		return this;
	}
};